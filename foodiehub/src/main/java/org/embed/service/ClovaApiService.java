package org.embed.service;

/**
 * 네이버 CLOVA Studio API 서비스
 */
public interface ClovaApiService {

    /**
     * 리뷰 내용들을 AI로 요약
     * @param reviewContents 결합된 리뷰 내용
     * @return 요약된 텍스트
     */
    String summarizeReviews(String reviewContents);
}
