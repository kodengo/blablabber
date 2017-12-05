package com.blablabber.gitlab.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class GitlabApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabApiClient.class);
    private static final String API_V4_MERGE_REQUESTS = "/api/v4/merge_requests/";
    private static final String API_V4_PROJECTS = "/api/v4/projects/";

    public List<GitLabMergeRequest> getMyGitLabMergeRequests(GitLabInfo gitLabInfo) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gitLabInfo.getBaseUrl() + API_V4_MERGE_REQUESTS)
                .queryParam("private_token", gitLabInfo.getToken());
        ResponseEntity<List<GitLabMergeRequest>> mergeRequestList = restTemplate.exchange(builder.toUriString(), GET, null, new ParameterizedTypeReference<List<GitLabMergeRequest>>() {
        });
        return mergeRequestList.getBody();
    }

    public List<GitLabMergeRequest> getAllOpenGitLabMergeRequests(GitLabInfo gitLabInfo) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gitLabInfo.getBaseUrl() + API_V4_MERGE_REQUESTS)
                .queryParam("state", "opened")
//                .queryParam("scope", "all")
                .queryParam("private_token", gitLabInfo.getToken());
        ResponseEntity<List<GitLabMergeRequest>> mergeRequestList = restTemplate.exchange(builder.toUriString(), GET, null, new ParameterizedTypeReference<List<GitLabMergeRequest>>() {
        });
        return mergeRequestList.getBody();
    }

    public GitLabMergeRequestChanges getMergeRequestChanges(GitLabInfo gitLabInfo, String projectId, String mergeRequestIid) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gitLabInfo.getBaseUrl() + API_V4_PROJECTS + projectId + "/merge_requests/" + mergeRequestIid + "/changes")
                .queryParam("private_token", gitLabInfo.getToken());
        ResponseEntity<GitLabMergeRequestChanges> mergeRequestList = restTemplate.exchange(builder.toUriString(), GET, null, new ParameterizedTypeReference<GitLabMergeRequestChanges>() {
        });
        return mergeRequestList.getBody();
    }

    public byte[] downloadFile(GitLabInfo gitLabInfo, String projectId, String fileLocation, String branch) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gitLabInfo.getBaseUrl() + API_V4_PROJECTS + projectId + "/repository/files/").pathSegment(escapeSlashes(fileLocation), "raw")
                .queryParam("ref", escapeSlashes(branch))
                .queryParam("private_token", gitLabInfo.getToken());

        URI url = builder.build(true).toUri();
        LOGGER.info(url.toString());
        try {
            ResponseEntity<byte[]> mergeRequestList = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<byte[]>() {
            });
            return mergeRequestList.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("Exception while getting file, probably cannot find deleted file.", e);
            return new byte[0];
        }
    }

    private String escapeSlashes(String string) {
        try {
            return URLEncoder.encode(string.replaceAll(" ", "%20"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}