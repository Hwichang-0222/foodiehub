document.addEventListener('DOMContentLoaded', () => {
	const form = document.getElementById('searchForm');
	const input = document.getElementById('keyword');

	form.addEventListener('submit', (e) => {
		e.preventDefault();

		const keyword = input.value.trim();

		if (keyword === '') {
			window.location.href = '/restaurant/list';
		} else {
			window.location.href = `/restaurant/search?keyword=${encodeURIComponent(keyword)}`;
		}
	});
});
