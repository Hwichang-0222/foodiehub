/**
 * 
 */

document.addEventListener("DOMContentLoaded", () => {
	const form = document.getElementById("searchForm");
	const input = document.getElementById("keyword");

	form.addEventListener("submit", (e) => {
		e.preventDefault();

		const keyword = input.value.trim();
	
		// 검색어가 없으면 전체 목록 페이지로 이동
	 	if (keyword === "") {
			window.location.href = "/restaurant/list";
		} else {
			// 검색어가 있으면 검색 페이지로 이동
			window.location.href = `/restaurant/list?keyword=${encodeURIComponent(keyword)}`;
		}
	});
});
