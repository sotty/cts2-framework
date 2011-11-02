package edu.mayo.cts2.framework.webapp.rest.controller;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.mayo.cts2.framework.model.exception.Cts2RestException;
import edu.mayo.cts2.framework.webapp.rest.command.QueryControl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="MethodTimingAspectTest.xml")
public class MethodTimingAspectTest {
	
	@Resource
	QueryControlTestBean queryControlTestBean;
	
	@Test
	public void testUnderTimeLimit(){
		QueryControl qc = new QueryControl();
		qc.setTimelimit(1);
		queryControlTestBean.testWithQueryControlUnderTime(qc);
	}
	
	@Test(expected=Cts2RestException.class)
	public void testOverTimeLimit() throws Exception {
		QueryControl qc = new QueryControl();
		qc.setTimelimit(1);
		queryControlTestBean.testWithQueryControlOverTime(qc);
	}
}