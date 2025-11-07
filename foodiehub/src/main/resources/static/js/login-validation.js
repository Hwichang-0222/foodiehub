/* ============================================
   로그인 페이지 유효성 검증
   - 로그인 실패시 얼럿창 표시
============================================ */

document.addEventListener("DOMContentLoaded", function () {
    /* ============================================
       로그인 실패 에러 메시지 처리
    ============================================ */
    
    // 에러 메시지 요소 확인
    const errorMessage = document.querySelector(".error-message");
    
    // 에러 메시지가 존재하면 얼럿창 표시
    if (errorMessage && errorMessage.textContent.trim()) {
        alert(errorMessage.textContent.trim());
    }
});