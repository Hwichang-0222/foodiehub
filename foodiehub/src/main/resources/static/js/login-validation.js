/* ==========================================
   로그인 에러 메시지 표시
========================================== */

document.addEventListener('DOMContentLoaded', () => {
	const errorMessage = document.querySelector('.error-message');

	// 에러 메시지가 있으면 alert로 표시
	if (errorMessage && errorMessage.textContent.trim()) {
		alert(errorMessage.textContent.trim());
	}
});
