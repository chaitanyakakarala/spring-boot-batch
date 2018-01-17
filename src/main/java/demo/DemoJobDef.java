package demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import reader.GenericFlatFileItemReader;
import writer.GenericJdbcBatchItemWriter;

@Configuration
@PropertySource(value = "classpath:application.properties")
@EnableBatchProcessing
public class DemoJobDef extends JobBase {

    @Value("${insertSqlString}")
    private String sqlString;

    @Value("${filePath}")
    private String filePath;

    @Value("${file.headers}")
    private String[] headerParams;

    @Value("${spring.batch.job.name}")
    private String jobName;

    @Value("${spring.batch.step.name}")
    private String stepName;

    @Value("${spring.batch.job.chunk.size}")
    private int jobChunkSize;

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new GenericFlatFileItemReader<Person>(filePath,headerParams, Person.class).getDelegate();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer() {
        return new GenericJdbcBatchItemWriter<Person>(sqlString,dataSource).getDeligate();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(jobName)
                                .incrementer(new RunIdIncrementer())
                                .listener(listener)
                                .flow(step1())
                                .end()
                                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get(stepName)
                .<Person, Person> chunk(jobChunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
}