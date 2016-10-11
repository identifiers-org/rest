package org.identifiers.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by sarala on 07/10/2016.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RestApplicationTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void testHome() throws Exception {

        this.mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("collections")));
    }

    @Test
    public void testCollections() throws Exception {
        this.mvc.perform(get("/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].name", equalTo("PubMed")))
                .andExpect(jsonPath("$[3].prefix", equalTo("pubmed")));
    }

    @Test
    public void testCollection() throws Exception {
        this.mvc.perform(
                get("/collections/MIR:00000015"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", equalTo("PubMed")))
                .andExpect(jsonPath("prefix", equalTo("pubmed")));
    }

    @Test
    public void testResource() throws Exception {
        this.mvc.perform(
                get("/resources/MIR:00100023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("info", equalTo("NCBI PubMed")));
    }

    /*TODO: checkRegexp() method in IdentifierController fails when using h2.
    * TODO: ^CHEBI:\\d+$ is istanciated as ^CHEBI:\\\\d+$.
    * TODO: The method works fine with mysql. */

/*    @Test
    public void testIdentifier() throws Exception {
        this.mvc.perform(
                get("/identifier/CHEBI:36927"))
                .andExpect(status().isOk());
    }

    @Test
    public void testIdentifierValidate() throws Exception {
        this.mvc.perform(
                get("/identifier/validate/CHEBI:36927"))
                .andExpect(status().isOk());
    }*/

}
