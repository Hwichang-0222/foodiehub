// /static/js/restaurant-detail.js
document.addEventListener('DOMContentLoaded', () => {

	/* ---------------------------------------------------
	[1] 탭 전환
	--------------------------------------------------- */
	const tabs = document.querySelectorAll('.tab');
	const contents = document.querySelectorAll('.tab-content');

	tabs.forEach(tab => {
		tab.addEventListener('click', () => {
			tabs.forEach(t => t.classList.remove('active'));
			contents.forEach(c => c.classList.remove('active'));

			tab.classList.add('active');
			document.getElementById(tab.dataset.tab).classList.add('active');

			if (tab.dataset.tab === 'map') initMap();
		});
	});


	/* ---------------------------------------------------
	[2] 리뷰 슬라이더
	--------------------------------------------------- */
	const reviews = document.querySelectorAll('.review-item');
	const prevBtn = document.querySelector('.prev-review');
	const nextBtn = document.querySelector('.next-review');
	let currentIndex = 0;

	function updateReview() {
		reviews.forEach((review, idx) =>
			review.classList.toggle('active', idx === currentIndex)
		);
		updateReviewCounter();
	}

	function updateReviewCounter() {
		const counter = document.querySelector('.review-counter');
		if (counter && reviews.length > 0) {
			counter.textContent = `${currentIndex + 1} / ${reviews.length}`;
		}
	}

	if (prevBtn && nextBtn && reviews.length > 0) {
		prevBtn.addEventListener('click', () => {
			currentIndex = (currentIndex - 1 + reviews.length) % reviews.length;
			updateReview();
		});

		nextBtn.addEventListener('click', () => {
			currentIndex = (currentIndex + 1) % reviews.length;
			updateReview();
		});

		document.addEventListener('keydown', (e) => {
			const reviewTab = document.getElementById('reviews');
			if (!reviewTab.classList.contains('active')) return;

			if (e.key === 'ArrowLeft') prevBtn.click();
			if (e.key === 'ArrowRight') nextBtn.click();
		});
	}

	/* ---------------------------------------------------
	[4] 이미지 모달
	--------------------------------------------------- */
	// 메뉴 이미지
	document.querySelectorAll('.menu-thumbnail').forEach(img => {
	    img.addEventListener('click', () => openImageModal(img.src));
	});

	// 리뷰 이미지
	document.querySelectorAll('.review-images img').forEach(img => {
	    img.addEventListener('click', () => openImageModal(img.src));
	});

	// 갤러리 이미지
	document.querySelectorAll('#gallery img').forEach(img => {
	    img.addEventListener('click', () => openImageModal(img.src));
	});

	function openImageModal(src) {
	    const modal = document.createElement('div');
	    modal.className = 'menu-image-modal';

	    modal.innerHTML = `
	        <div class="menu-image-modal-content">
	            <span class="menu-image-modal-close">&times;</span>
	            <img src="${src}">
	        </div>
	    `;

	    document.body.appendChild(modal);
	    setTimeout(() => modal.classList.add('active'), 10);

	    modal.querySelector('.menu-image-modal-close')
	        .addEventListener('click', () => closeImageModal(modal));

	    modal.addEventListener('click', e => {
	        if (e.target === modal) closeImageModal(modal);
	    });

	    document.addEventListener('keydown', function esc(e) {
	        if (e.key === 'Escape') {
	            closeImageModal(modal);
	            document.removeEventListener('keydown', esc);
	        }
	    });
	}

	function closeImageModal(modal) {
	    modal.classList.remove('active');
	    setTimeout(() => modal.remove(), 250);
	}

	/* ---------------------------------------------------
	[5] 메뉴 더보기 버튼
	--------------------------------------------------- */
	const moreBtn = document.getElementById("menuMoreBtn");
	if (moreBtn) {
		moreBtn.addEventListener('click', () => {
			document.querySelectorAll(".hidden-menu").forEach(m => m.style.display = "block");
			moreBtn.style.display = "none";
		});
	}


	/* ---------------------------------------------------
	[6] 로그인 여부 체크 후: 리뷰, 댓글 작성 제한
	--------------------------------------------------- */
	const loggedInInput = document.querySelector('input[name="userLoggedIn"]');
	const isLoggedIn = loggedInInput ? loggedInInput.value === 'true' : false;

	// 댓글
	document.querySelectorAll('.comment-form form').forEach(form => {
		form.addEventListener('submit', e => {
			if (!isLoggedIn) {
				e.preventDefault();
				alert('로그인 후 이용 가능합니다.');
				location.href = '/user/login';
			}
		});
	});

	// 리뷰
	const reviewForm = document.querySelector('#write form');
	if (reviewForm) {
		reviewForm.addEventListener('submit', e => {
			if (!isLoggedIn) {
				e.preventDefault();
				alert('로그인 후 이용 가능합니다.');
				location.href = '/user/login';
			}
		});
	}


	/* ---------------------------------------------------
	[7] 댓글 textarea Auto-Height
	--------------------------------------------------- */
	document.querySelectorAll('.comment-form textarea').forEach(area => {
		area.addEventListener('input', function () {
			this.style.height = 'auto';
			this.style.height = (this.scrollHeight) + 'px';
		});
	});


	/* ---------------------------------------------------
	[8] 지도 초기화
	--------------------------------------------------- */
	function initMap() {
		const mapContainer = document.getElementById('map');
		if (!mapContainer) return;

		const lat = parseFloat(mapContainer.dataset.lat);
		const lng = parseFloat(mapContainer.dataset.lng);
		if (!lat || !lng) return;

		const map = new kakao.maps.Map(mapContainer, {
			center: new kakao.maps.LatLng(lat, lng),
			level: 3
		});

		new kakao.maps.Marker({
			position: new kakao.maps.LatLng(lat, lng),
			map: map
		});
	}

	// 페이지 첫 진입 시 map 탭이면 바로 표시
	const activeTab = document.querySelector('.tab.active');
	if (activeTab && activeTab.dataset.tab === 'map') initMap();

});
