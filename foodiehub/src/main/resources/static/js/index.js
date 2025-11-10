/* ==========================================
   메인 페이지 검색 기능
========================================== */

document.addEventListener('DOMContentLoaded', () => {
	const form = document.getElementById('searchForm');
	const input = document.getElementById('keyword');

	// 검색 폼 제출 이벤트
	form.addEventListener('submit', (e) => {
		e.preventDefault();

		const keyword = input.value.trim();

		// 키워드가 없으면 전체 리스트로 이동
		if (keyword === '') {
			window.location.href = '/restaurant/list';
		} else {
			// 키워드가 있으면 검색 페이지로 이동
			window.location.href = `/restaurant/search?keyword=${encodeURIComponent(keyword)}`;
		}
	});
});
