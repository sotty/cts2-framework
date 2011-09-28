/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.cts2.framework.webapp.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.mayo.cts2.framework.model.association.Association;
import edu.mayo.cts2.framework.model.association.AssociationDirectory;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.association.AssociationGraph;
import edu.mayo.cts2.framework.model.association.AssociationMsg;
import edu.mayo.cts2.framework.model.association.types.GraphDirection;
import edu.mayo.cts2.framework.model.core.FilterComponent;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectory;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.service.core.QueryControl;
import edu.mayo.cts2.framework.service.command.Filter;
import edu.mayo.cts2.framework.service.command.Page;
import edu.mayo.cts2.framework.service.command.restriction.AssociationQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.association.AdvancedAssociationQueryService;
import edu.mayo.cts2.framework.service.profile.association.AssociationMaintenanceService;
import edu.mayo.cts2.framework.service.profile.association.AssociationQueryService;
import edu.mayo.cts2.framework.service.profile.association.AssociationReadService;
import edu.mayo.cts2.framework.service.profile.association.id.AssociationId;
import edu.mayo.cts2.framework.service.profile.entitydescription.id.EntityDescriptionId;

/**
 * The Class AssociationController.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Controller
public class AssociationController extends AbstractServiceAwareController {
	
	@Cts2Service
	private AssociationReadService associationReadService;
	
	@Cts2Service
	private AssociationMaintenanceService associationMaintenanceService;
	
	@Cts2Service
	private AssociationQueryService associationQueryService;
	
	@Cts2Service
	private AdvancedAssociationQueryService advancedAssociationQueryService;
	
	/**
	 * Gets the children associations of entity.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param filter the filter
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param entityName the entity name
	 * @return the children associations of entity
	 */
	@RequestMapping(value=PATH_CHILDREN_ASSOCIATIONS_OF_ENTITY, method=RequestMethod.GET)
	@ResponseBody
	public AssociationDirectory getChildrenAssociationsOfEntity(
			HttpServletRequest httpServletRequest,
			QueryControl queryControl,
			Filter filter,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@PathVariable(VAR_ENTITYID) String entityName) {
		
		return this.getChildrenAssociationsOfEntity(
				httpServletRequest, 
				null, 
				filter, 
				page, 
				codeSystemName, 
				codeSystemVersionName,
				entityName);
	}
	
	/**
	 * Gets the children associations of entity.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param query the query
	 * @param filter the filter
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param entityName the entity name
	 * @return the children associations of entity
	 */
	@RequestMapping(value=PATH_CHILDREN_ASSOCIATIONS_OF_ENTITY, method=RequestMethod.POST)
	@ResponseBody
	public EntityDirectory getChildrenAssociationsOfEntity(
			HttpServletRequest httpServletRequest,
			QueryControl queryControl,
			Query query,
			Filter filter,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@PathVariable(VAR_ENTITYID) String entityName) {
		
		FilterComponent filterComponent = this.processFilter(filter, this.associationQueryService);
		
		EntityDescriptionId id = new EntityDescriptionId();
		id.setCodeSystemVersion(codeSystemVersionName);
		id.setName(this.getScopedEntityName(entityName, codeSystemName));
		
		DirectoryResult<EntityDirectoryEntry> directoryResult = 
			this.associationQueryService.getChildrenAssociationsOfEntity(
					query, 
					filterComponent,
					page,
					id);
		
		EntityDirectory directory = this.populateDirectory(
				directoryResult, 
				page, 
				httpServletRequest, 
				EntityDirectory.class);
		
		return directory;
	}
	
	/**
	 * Gets the parent associations of entity.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param query the query
	 * @param filter the filter
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param entityName the entity name
	 * @return the parent associations of entity
	 */
	@RequestMapping(value=PATH_PARENT_ASSOCIATIONS_OF_ENTITY, method=RequestMethod.POST)
	@ResponseBody
	public EntityDirectory getParentAssociationsOfEntity(
			HttpServletRequest httpServletRequest,
			@RequestBody Query query,
			Filter filter,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@PathVariable(VAR_ENTITYID) String entityName) {
		
		FilterComponent filterComponent = this.processFilter(filter, this.associationQueryService);
		
		DirectoryResult<EntityDirectoryEntry> directoryResult = 
			this.associationQueryService.getParentAssociationsOfEntity(
					query,
					filterComponent,
					page,
					EntityDescriptionId.buildEntityDescriptionId(
							codeSystemVersionName, 
							this.getScopedEntityName(entityName, codeSystemName)));
		
		EntityDirectory directory = this.populateDirectory(
				directoryResult, 
				page, 
				httpServletRequest, 
				EntityDirectory.class);
		
		return directory;
	}
	
	/**
	 * Gets the associations of code system version.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param filter the filter
	 * @param associationRestrictions the association restrictions
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @return the associations of code system version
	 */
	@RequestMapping(value=PATH_ASSOCIATIONS_OF_CODESYSTEMVERSION, method=RequestMethod.GET)
	@ResponseBody
	public AssociationDirectory getAssociationsOfCodeSystemVersion(
			HttpServletRequest httpServletRequest,
			Filter filter,
			AssociationQueryServiceRestrictions associationRestrictions,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName) {
		
		associationRestrictions.setCodesystemversion(codeSystemVersionName);
		
		return 
				this.getAssociations(
						httpServletRequest, 
						null, 
						associationRestrictions, 
						page);
	}
	
	/**
	 * Gets the associations.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param query the query
	 * @param filter the filter
	 * @param associationRestrictions the association restrictions
	 * @param page the page
	 * @return the associations
	 */
	@RequestMapping(value=PATH_ASSOCIATIONS, method=RequestMethod.POST)
	@ResponseBody
	public AssociationDirectory getAssociations(
			HttpServletRequest httpServletRequest,
			Query query,
			Filter filter,
			AssociationQueryServiceRestrictions associationRestrictions,
			Page page) {
	
		FilterComponent filterComponent = this.processFilter(filter, this.associationQueryService);
		
		DirectoryResult<AssociationDirectoryEntry> directoryResult = 
			this.associationQueryService.getResourceSummaries(query, filterComponent, associationRestrictions, page);
		
		AssociationDirectory directory = this.populateDirectory(
				directoryResult, 
				page, 
				httpServletRequest, 
				AssociationDirectory.class);
		
		return directory;
	}

	/**
	 * Gets the associations.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param filter the filter
	 * @param associationRestrictions the association restrictions
	 * @param page the page
	 * @return the associations
	 */
	@RequestMapping(value=PATH_ASSOCIATIONS, method=RequestMethod.GET)
	@ResponseBody
	public AssociationDirectory getAssociations(
			HttpServletRequest httpServletRequest,
			Filter filter,
			AssociationQueryServiceRestrictions associationRestrictions,
			Page page) {
		
		return 
				this.getAssociations(
						httpServletRequest, 
						null, 
						filter,
						associationRestrictions, 
						page);
	}

	/**
	 * Gets the association by name.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param associationName the association name
	 * @return the association by name
	 */
	@RequestMapping(value=PATH_ASSOCIATIONBYID, method=RequestMethod.GET)
	@ResponseBody
	public AssociationMsg getAssociationByName(
			HttpServletRequest httpServletRequest,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@PathVariable(VAR_ASSOCIATIONID) String associationName) {
		
		Association association = 
			this.associationReadService.read(
					AssociationId.buildAssociationId(codeSystemVersionName, associationName));
		
		AssociationMsg msg = new AssociationMsg();
		msg.setAssociation(association);
		
		msg = this.wrapMessage(msg, httpServletRequest);
		
		return msg;
	}
	
	/**
	 * Gets the graph code system version.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param associationRestrictions the association restrictions
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param focus the focus
	 * @param direction the direction
	 * @param depth the depth
	 * @return the graph code system version
	 */
	@RequestMapping(value=PATH_GRAPH_OF_CODESYSTEMVERSION, method=RequestMethod.GET)
	@ResponseBody
	public AssociationGraph getGraphCodeSystemVersion(
			HttpServletRequest httpServletRequest,
			AssociationQueryServiceRestrictions associationRestrictions,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@RequestParam(required=true, defaultValue="TOP_NODE") String focus,
			@RequestParam(required=true, defaultValue="FORWARD") GraphDirection direction,
			@RequestParam(required=true, defaultValue="1") int depth) {

		AssociationGraph directoryResult = 
			this.advancedAssociationQueryService.getAssociationGraph(
					EntityDescriptionId.buildEntityDescriptionId(
							codeSystemVersionName, 
							this.getScopedEntityName(focus, codeSystemName)),
					GraphDirection.FORWARD,
					depth);

		directoryResult.setHeading(this.getHeading(httpServletRequest));
		
		return directoryResult;
	}
	
	/**
	 * Gets the source of associations of entity.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param query the query
	 * @param associationRestrictions the association restrictions
	 * @param filter the filter
	 * @param page the page
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param entityName the entity name
	 * @return the source of associations of entity
	 */
	@RequestMapping(value=PATH_SOURCEOF_ASSOCIATIONS_OF_ENTITY, method=RequestMethod.POST)
	@ResponseBody
	public AssociationDirectory getSourceOfAssociationsOfEntity(
			HttpServletRequest httpServletRequest,
			Query query,
			AssociationQueryServiceRestrictions associationRestrictions,
			Filter filter,
			Page page,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@PathVariable(VAR_ENTITYID) String entityName) {

		associationRestrictions.setSourceentity(entityName);
		
		return this.getAssociationsOfCodeSystemVersion(
				httpServletRequest, 
				filter,
				associationRestrictions, 
				page, 
				codeSystemName,
				codeSystemVersionName);
	}
	
	/**
	 * Creates the association.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param association the association
	 * @param changeseturi the changeseturi
	 * @param codeSystemName the code system name
	 * @param codeSystemVersionName the code system version name
	 * @param associationName the association name
	 */
	@RequestMapping(value=PATH_ASSOCIATIONBYID, method=RequestMethod.PUT)
	@ResponseBody
	public void createAssociation(
			HttpServletRequest httpServletRequest,
			@RequestBody Association association,
			@RequestParam(required=false) String changeseturi,
			@PathVariable(VAR_CODESYSTEMID) String codeSystemName,
			@PathVariable(VAR_CODESYSTEMVERSIONID) String codeSystemVersionName,
			@PathVariable(VAR_ASSOCIATIONID) String associationName) {
			
		this.associationMaintenanceService.createResource(changeseturi, association);
	}
}