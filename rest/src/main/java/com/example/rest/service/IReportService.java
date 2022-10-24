package com.example.rest.service;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.response.ReportResponse;

public interface IReportService {

    CommonResponse<ReportResponse> reportPost(String id, String token, String subject, String details);
}
