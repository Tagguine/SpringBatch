package com.demo;

import com.model.Utilisateur;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {




        @Autowired
        public JobBuilderFactory jobBuilderFactory;

        @Autowired
        public StepBuilderFactory stepBuilderFactory;

        @Autowired
        public DataSource dataSource;

        @Bean
        public DataSource dataSource() {
            final DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost/springbatch");
            dataSource.setUsername("root");
            dataSource.setPassword("");

            return dataSource;
        }


    @Bean
    public FlatFileItemReader<Utilisateur> reader(){
        FlatFileItemReader<Utilisateur> reader = new FlatFileItemReader<Utilisateur>();
        reader.setResource(new ClassPathResource("utilisateurs.csv"));
        reader.setLineMapper(new DefaultLineMapper<Utilisateur>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "name" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Utilisateur>() {{
                setTargetType(Utilisateur.class);
            }});

        }});

        return reader;
    }
    @Bean
    public UtilisateurProcessor processor(){
        return new UtilisateurProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Utilisateur> writer(){
        JdbcBatchItemWriter<Utilisateur> writer = new JdbcBatchItemWriter<Utilisateur>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Utilisateur>());
        writer.setSql("INSERT INTO utilisateur(name) VALUES (:name)");
        writer.setDataSource(dataSource);

        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Utilisateur, Utilisateur> chunk(3)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

}
