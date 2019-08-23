package de.scrappel.data.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.ExistsFunction;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.Function;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;

import de.scrappel.data.solr.product.Product;
import de.scrappel.data.solr.product.ProductRepository;


@SpringBootApplication
public class SolrApplication {

	private static final Logger log = LoggerFactory.getLogger(SolrApplication.class);

	@Autowired
	private SolrTemplate solrTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SolrApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProductRepository repository) {
		return (args) -> {

			SimpleQuery groupQuery = new SimpleQuery(new SimpleStringCriteria("*:*"));

			GroupOptions groupOptions = new GroupOptions();
			groupQuery.setGroupOptions(groupOptions);
			groupOptions.addGroupByField("author");
			groupOptions.setLimit(1);


			GroupPage<Product> groupResultPage = solrTemplate.queryForGroupPage("techproducts", groupQuery, Product.class);
			GroupResult<Product> fieldGroup = groupResultPage.getGroupResult("author");

			System.out.println("Matches count: " + fieldGroup.getMatches());
			System.out.println("Groups size: " + fieldGroup.getGroupEntries().getTotalElements());

			fieldGroup.getGroupEntries().forEach(x ->
					System.out.println("- " + x.getGroupValue() + " : " +  x.getResult().getTotalElements()));
		};
	}

}
