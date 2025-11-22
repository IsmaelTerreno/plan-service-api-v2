package com.remotejob.planservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remotejob.planservice.entity.Plan;
import com.remotejob.planservice.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * This class contains unit tests for the PlanService application.
 * It extends the TestUtils class to utilize common test utilities.
 */
class PlanServiceAPIApplicationTests extends TestUtils {

    @Test
    void shouldGetPublicSearch() throws Exception {
        // Get JWT token
        String JWT = this.registerUserAndGetJWT();
        // Convert the newJob object into a JSON string
        String newJobJson = convertToJson(setupTestPlan(new Plan()));
        // Create a new plan
        performPostRequest(newJobJson, "/api/v1/plan", JWT)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        // Get all jobs by text search
        MvcResult responseSearch = performGetRequest("/api/v1/plan/search?textToSearch=Remote", "")
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // Get jobs found from response
        List<Plan> plans = getDataListResponse(responseSearch, Plan.class);
        // Check that the jobs were found
        assert !plans.isEmpty();
    }

    @Test
    void shouldGetJobsByUserId() throws Exception {
        // Get JWT token
        String JWT = this.registerUserAndGetJWT();
        Plan newPlan = setupTestPlan(new Plan());
        // Convert the newJob object into a JSON string
        String newJobJson = convertToJson(newPlan);
        // Create a new job
        performPostRequest(newJobJson, "/api/v1/plan", JWT)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        // Get all jobs by text search
        MvcResult responseSearch = performGetRequest("/api/v1/plan/user/" + newPlan.getUserId(), "")
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // Get jobs found from response
        List<Plan> plans = getDataListResponse(responseSearch, Plan.class);
        // Check that the jobs were found
        assert !plans.isEmpty();
    }

    @Test
    void shouldGetJobById() throws Exception {
        // Get JWT token
        String JWT = this.registerUserAndGetJWT();
        Plan newPlan = setupTestPlan(new Plan());
        // Convert the newJob object into a JSON string
        String newJobJson = convertToJson(newPlan);
        // Create a new job
        MvcResult responseCreate = performPostRequest(newJobJson, "/api/v1/plan", JWT)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Plan createdPlan = getDataResponse(responseCreate, Plan.class);
        String getJobByIdUrl = "/api/v1/plan/" + createdPlan.getId();
        // Get all jobs by text search
        MvcResult responseSearch = performGetRequest(getJobByIdUrl, "")
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // Get job found from response
        Plan plan = getDataResponse(responseSearch, Plan.class);
        // Check that the job were found and the id is the same
        assert plan.getId().equals(createdPlan.getId());
    }

    @Test
    void shouldCreateJob() throws Exception {
        // Get JWT token
        String JWT = this.registerUserAndGetJWT();
        // Convert the newJob object into a JSON string
        String newJobJson = convertToJson(setupTestPlan(new Plan()));
        // Create a new job
        MvcResult responseCreated = performPostRequest(newJobJson, "/api/v1/plan", JWT)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // Get Job from response
        Plan createdPlan = getDataResponse(responseCreated, Plan.class);
        // Check that id is not null
        assert createdPlan.getId() != null;
    }

    @Test
    void shouldUpdateJob() throws Exception {
        // Get JWT token
        String JWT = this.registerUserAndGetJWT();
        // Convert the newJob object into a JSON string
        String newJobJson = convertToJson(setupTestPlan(new Plan()));
        // Create a new job
        MvcResult responseCreated = performPostRequest(newJobJson, "/api/v1/plan", JWT)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // Get Job from response
        Plan createdPlan = getDataResponse(responseCreated, Plan.class);
        // Update the title of the created job
        createdPlan.setTitle("Remote Backend Javascript Engineer");
        // Convert the createdJob object into a JSON string
        String updatedJobJson = convertToJson(createdPlan);
        // Update the job
        MvcResult responseUpdate =
                performPutRequest(updatedJobJson, "/api/v1/plan", JWT)
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        // Get Job from response
        Plan updatedPlan = getDataResponse(responseUpdate, Plan.class);
        // Check that the title has been updated
        assert updatedPlan.getTitle().equals("Remote Backend Javascript Engineer");
    }

    @Test
    void shouldDeleteJob() throws Exception {
        // Get JWT token
        String JWT = this.registerUserAndGetJWT();
        // Convert the newJob object into a JSON string
        String newJobJson = convertToJson(setupTestPlan(new Plan()));
        // Create a new job
        MvcResult responseCreated = performPostRequest(newJobJson, "/api/v1/plan", JWT)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // Get Job from response
        Plan createdPlan = getDataResponse(responseCreated, Plan.class);
        // Convert the createdJob object into a JSON string
        String updatedJobJson = convertToJson(createdPlan);
        // Delete the job
        MvcResult responseDelete =
                performDeleteRequest(updatedJobJson, "/api/v1/plan/" + createdPlan.getId(), JWT)
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        // Get Job from response
        String deletedResponse = getMessageResponse(responseDelete, Plan.class);
        // Check that the title has been updated
        assert deletedResponse.equals("Plan deleted successfully");
    }

    /**
     * Sets up a test Job object with predefined data values.
     *
     * @param planTest The Job object to be set up with test data.
     * @return The Job object populated with test values.
     */
    private Plan setupTestPlan(Plan planTest) {
        String title = "Remote Fullstack Javascript Engineer";
        String detailString = "{" +
                "    \"blocks\": [" +
                "      {" +
                "        \"key\": \"46ani\"," +
                "        \"text\": \"Requirements\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"nbd9\"," +
                "        \"text\": \"Previous experience as a full stack engineer\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"b4o7o\"," +
                "        \"text\": \"Advanced knowledge of one or more of the following frontend languages: HTML5, CSS, JavaScript, PHP, and JQuery\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"bav5m\"," +
                "        \"text\": \"Proficient in one or more of the following backend languages: Java, Python, Rails, Ruby, .NET, and NodeJS\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"9344j\"," +
                "        \"text\": \"Knowledge of database systems and SQL\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"fjcop\"," +
                "        \"text\": \"Advanced troubleshooting skills\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"cf0u3\"," +
                "        \"text\": \"Familiarity with JavaScript frameworks\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }," +
                "      {" +
                "        \"key\": \"fv6qt\"," +
                "        \"text\": \"Good communication skills\"," +
                "        \"type\": \"unstyled\"," +
                "        \"depth\": 0," +
                "        \"inlineStyleRanges\": []," +
                "        \"entityRanges\": []," +
                "        \"data\": {}" +
                "      }" +
                "    ]," +
                "    \"entityMap\": {}" +
                "  }";
        planTest.setUserId(this.generateRandomString(10));
        planTest.setCompanyId(this.generateRandomString(10) + "-company-test");
        planTest.setTitle(title);
        planTest.setCategory("programming");
        planTest.setRegionalRestrictions("US, Argentina");
        planTest.setHowToApply("https://about.google/apply");
        planTest.setType("contract");
        try {
            planTest.setDetail(new ObjectMapper().readTree(detailString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        planTest.setSalaryFrom(100000);
        planTest.setSalaryTo(120000);
        planTest.setBenefits("Health insurance, 401k");
        planTest.setPriorityResult(0);
        planTest.setCreatedAt(System.currentTimeMillis());
        planTest.setUpdatedAt(System.currentTimeMillis());
        return planTest;
    }

}