package com.polymath.jobboard.utils;

import com.polymath.jobboard.models.JobSeekers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateTsQuery {
    public static String generateTsQuery(JobSeekers jobSeekers) {
        List<String> terms = new ArrayList<>();
        terms.addAll(Arrays.asList(jobSeekers.getSkills().split("\\s*,\\s*")));
        terms.addAll(Arrays.asList(jobSeekers.getExperiences().split("\\s*,\\s*")));
        terms=terms.stream().map(String::toLowerCase).distinct().toList();
        return String.join(" | ", terms);
    }
}
