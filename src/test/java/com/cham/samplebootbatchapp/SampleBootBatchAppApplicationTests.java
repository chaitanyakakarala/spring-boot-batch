package com.cham.samplebootbatchapp;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import com.cham.samplebootbatchapp.DemoJobDef;
import com.cham.samplebootbatchapp.pojo.Person;

import junit.framework.Assert;

@Profile("int")
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes={SampleBootBatchAppApplication.class,DemoJobDef.class})
public class SampleBootBatchAppApplicationTests {

	// ref: https://docs.spring.io/spring-batch/4.0.x/reference/html/testing.html#testing
	private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Before
	public void setUp(){
		
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void hasTheJobCompleted() throws Exception {
		
		List<String> personList = jdbcTemplate
				.queryForList("SELECT first_name from people",String.class);
		System.out.println(personList);
		Assert.assertNotNull(personList);
		Assert.assertEquals(5, personList.size());
		
	}

}
