package com.cham.samplebootbatchapp;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@ActiveProfiles("int")
/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DriverManagerDataSource.class,DataSource.class,TestConfiguration.class,JobBase.class,JdbcTemplate.class,JobCompletionNotificationListener.class})
@ContextConfiguration(classes={DemoJobDef.class})*/
@EnableBatchProcessing
@RunWith(SpringJUnit4ClassRunner.class )
@SpringBootTest(classes={DemoJobDef.class,SampleBootBatchAppApplication.class })
@ContextConfiguration(classes={DemoJobDef.class})
public class SampleBootBatchAppApplicationTests2 {

	// ref: https://docs.spring.io/spring-batch/4.0.x/reference/html/testing.html#testing

	@Autowired
	DataSource dataSource;
	
	private JobLauncherTestUtils jobLauncherTestUtils =new JobLauncherTestUtils();
	
		public JobRepository getJobRepository() throws Exception {
			JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
			factory.setDataSource(dataSource);
			factory.setTransactionManager(new DataSourceTransactionManager(dataSource));
			factory.afterPropertiesSet();
			return  (JobRepository) factory.getObject();
		}
	 

	 
		public JobLauncher getJobLauncher() throws Exception {
			SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
			jobLauncher.setJobRepository(getJobRepository());
			jobLauncher.afterPropertiesSet();
			return jobLauncher;
		}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	org.springframework.batch.core.Job job;
	
	/*@Autowired
	TestBatch testBatch;*/
	
	@Before
	public void setUp() throws Exception{
		
		jobLauncherTestUtils.setJobLauncher(getJobLauncher());
		jobLauncherTestUtils.setJobRepository(getJobRepository());
		jobLauncherTestUtils.setJob(job);
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void hasTheJobCompleted() throws Exception {
		
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
		
	}

}
