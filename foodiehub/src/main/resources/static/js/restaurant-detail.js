// /static/js/restaurant-detail.js
document.addEventListener('DOMContentLoaded', () => {

	// 1. 탭 전환
	const tabs = document.querySelectorAll('.tab');
	const contents = document.querySelectorAll('.tab-content');

	tabs.forEach(tab => {
		tab.addEventListener('click', () => {
			tabs.forEach(t => t.classList.remove('active'));
			contents.forEach(c => c.classList.remove('active'));
			tab.classList.add('active');
			const target = document.getElementById(tab.dataset.tab);
			target.classList.add('active');

			// 지도 탭 클릭 시 지도 초기화
			if (tab.dataset.tab === 'map') {
				initMap();
			}
		});
	});

	// 2. 리뷰 슬라이더
	const reviews = document.querySelectorAll('.review-item');
	const prevBtn = document.querySelector('.prev-review');
	const nextBtn = document.querySelector('.next-review');
	let currentIndex = 0;

	function updateReview() {
		reviews.forEach((review, idx) => {
			review.classList.toggle('active', idx === currentIndex);
		});
		
		// 현재 리뷰 번호 표시 (선택사항)
		updateReviewCounter();
	}

	// 리뷰 카운터 업데이트
	function updateReviewCounter() {
		const counterElement = document.querySelector('.review-counter');
		if (counterElement && reviews.length > 0) {
			counterElement.textContent = `${currentIndex + 1} / ${reviews.length}`;
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

		// 키보드 네비게이션 추가
		document.addEventListener('keydown', (e) => {
			// 리뷰 탭이 활성화되어 있을 때만 작동
			const reviewTab = document.getElementById('reviews');
			if (!reviewTab || !reviewTab.classList.contains('active')) return;

			if (e.key === 'ArrowLeft') {
				prevBtn.click();
			} else if (e.key === 'ArrowRight') {
				nextBtn.click();
			}
		});

		// 버튼 비활성화 처리 (첫/마지막 리뷰일 때)
		function updateButtonStates() {
			if (reviews.length <= 1) {
				prevBtn.style.opacity = '0.3';
				nextBtn.style.opacity = '0.3';
				prevBtn.style.cursor = 'not-allowed';
				nextBtn.style.cursor = 'not-allowed';
			}
		}
		updateButtonStates();
	}

	// 3. 이미지 확대 모달
	const reviewImages = document.querySelectorAll('.review-images img');
	
	reviewImages.forEach(img => {
		img.addEventListener('click', () => {
			openImageModal(img.src);
		});
	});

	function openImageModal(imageSrc) {
		// 모달 생성
		const modal = document.createElement('div');
		modal.className = 'image-modal';
		modal.innerHTML = `
			<div class="image-modal-content">
				<span class="image-modal-close">&times;</span>
				<img src="${imageSrc}" alt="확대 이미지">
			</div>
		`;
		
		document.body.appendChild(modal);
		
		// 애니메이션을 위한 약간의 지연
		setTimeout(() => {
			modal.classList.add('active');
		}, 10);

		// 닫기 버튼 이벤트
		const closeBtn = modal.querySelector('.image-modal-close');
		closeBtn.addEventListener('click', () => closeImageModal(modal));

		// 배경 클릭 시 닫기
		modal.addEventListener('click', (e) => {
			if (e.target === modal) {
				closeImageModal(modal);
			}
		});

		// ESC 키로 닫기
		const escHandler = (e) => {
			if (e.key === 'Escape') {
				closeImageModal(modal);
				document.removeEventListener('keydown', escHandler);
			}
		};
		document.addEventListener('keydown', escHandler);
	}

	function closeImageModal(modal) {
		modal.classList.remove('active');
		setTimeout(() => {
			modal.remove();
		}, 300);
	}

	// 4. 로그인 여부 확인 - 버그 수정!
	// 이전: const isLoggedIn = document.body.dataset.loggedIn === 'true';
	// 문제: data-logged-in="${user != null}"이 Thymeleaf로 평가되지 않음
	// 해결: Thymeleaf inline 표현식 또는 hidden input 사용
	
	const loggedInInput = document.querySelector('input[name="userLoggedIn"]');
	const isLoggedIn = loggedInInput ? loggedInInput.value === 'true' : false;
	
	// 디버깅 로그
	console.log('[restaurant-detail.js] isLoggedIn:', isLoggedIn);
	console.log('[restaurant-detail.js] loggedInInput value:', loggedInInput ? loggedInInput.value : 'not found');

	// 댓글 작성 폼 제출 제어
	document.querySelectorAll('.comment-form form').forEach(form => {
		form.addEventListener('submit', e => {
			if (!isLoggedIn) {
				e.preventDefault();
				alert('로그인 후 이용 가능합니다.');
				window.location.href = '/user/login';
			}
		});
	});

	// 리뷰 작성 폼 제출 제어
	const reviewForm = document.querySelector('#write form');
	if (reviewForm) {
		reviewForm.addEventListener('submit', e => {
			if (!isLoggedIn) {
				e.preventDefault();
				alert('로그인 후 이용 가능합니다.');
				window.location.href = '/user/login';
			}
		});
	}

	// 5. 댓글 textarea 자동 높이 조절
	const commentTextareas = document.querySelectorAll('.comment-form textarea');
	commentTextareas.forEach(textarea => {
		textarea.addEventListener('input', function() {
			this.style.height = 'auto';
			this.style.height = (this.scrollHeight) + 'px';
		});
	});

	// 6. 지도 초기화 함수
	function initMap() {
		const mapContainer = document.getElementById('map');
		if (!mapContainer) return;

		const lat = parseFloat(mapContainer.dataset.lat);
		const lng = parseFloat(mapContainer.dataset.lng);

		if (!lat || !lng) {
			console.warn('지도 좌표가 없습니다.');
			return;
		}

		const mapOption = {
			center: new kakao.maps.LatLng(lat, lng),
			level: 3
		};

		const map = new kakao.maps.Map(mapContainer, mapOption);

		const marker = new kakao.maps.Marker({
			position: new kakao.maps.LatLng(lat, lng)
		});
		marker.setMap(map);
	}

	// 페이지 첫 로드 시 지도 탭이 활성화되어 있다면 지도도 표시
	const activeTab = document.querySelector('.tab.active');
	if (activeTab && activeTab.dataset.tab === 'map') {
		initMap();
	}

});

/* 이미지 모달 스타일 추가 */
const modalStyles = `
	<style>
		.image-modal {
			position: fixed;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-color: rgba(0, 0, 0, 0);
			display: flex;
			justify-content: center;
			align-items: center;
			z-index: 9999;
			opacity: 0;
			transition: all 0.3s ease;
		}

		.image-modal.active {
			background-color: rgba(0, 0, 0, 0.9);
			opacity: 1;
		}

		.image-modal-content {
			position: relative;
			max-width: 90%;
			max-height: 90%;
			transform: scale(0.7);
			transition: transform 0.3s ease;
		}

		.image-modal.active .image-modal-content {
			transform: scale(1);
		}

		.image-modal-content img {
			max-width: 100%;
			max-height: 90vh;
			border-radius: 12px;
			box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
		}

		.image-modal-close {
			position: absolute;
			top: -40px;
			right: 0;
			font-size: 36px;
			color: white;
			cursor: pointer;
			background-color: rgba(0, 0, 0, 0.5);
			width: 40px;
			height: 40px;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			transition: all 0.3s ease;
		}

		.image-modal-close:hover {
			background-color: rgba(54, 209, 182, 0.8);
			transform: rotate(90deg);
		}

		/* 리뷰 카운터 스타일 */
		.review-counter {
			text-align: center;
			margin-top: 15px;
			font-size: 0.9rem;
			color: #777;
			font-weight: 600;
		}
	</style>
`;

// 스타일 추가
if (!document.querySelector('#modal-styles')) {
	const styleElement = document.createElement('div');
	styleElement.id = 'modal-styles';
	styleElement.innerHTML = modalStyles;
	document.head.appendChild(styleElement.firstElementChild);
}