/* ==========================================
   관리자 대시보드 초기화
========================================== */

document.addEventListener('DOMContentLoaded', () => {

	/* ==========================================
	   공통 업데이트 요청 함수
	========================================== */
	const sendUpdateRequest = (url, confirmMsg, successMsg) => {
		if (confirm(confirmMsg)) {
			fetch(url, { method: 'GET' })
				.then((response) => {
					if (response.ok) {
						alert(successMsg);
						location.reload();
					} else {
						alert('요청 처리 중 오류가 발생했습니다.');
					}
				})
				.catch((err) => {
					console.error(err);
					alert('서버 요청 중 오류가 발생했습니다.');
				});
		}
	};

	/* ==========================================
	   탭 전환 기능
	========================================== */
	const tabs = document.querySelectorAll('.admin-tab');
	const contents = document.querySelectorAll('.tab-content');

	// 로컬 스토리지에서 마지막 활성 탭 복원
	const savedTab = localStorage.getItem('activeAdminTab');
	if (savedTab) {
		contents.forEach((c) => c.classList.remove('active'));
		tabs.forEach((t) => t.classList.remove('active'));

		const targetTab = document.querySelector(savedTab);
		if (targetTab) {
			targetTab.classList.add('active');
			const btn = document.querySelector(`[data-target="${savedTab}"]`);
			if (btn) btn.classList.add('active');
		}
	}

	// 탭 클릭 이벤트
	tabs.forEach((tab) => {
		tab.addEventListener('click', () => {
			const target = tab.dataset.target;

			tabs.forEach((t) => t.classList.remove('active'));
			contents.forEach((c) => c.classList.remove('active'));

			tab.classList.add('active');
			document.querySelector(target).classList.add('active');

			// 현재 활성 탭을 로컬 스토리지에 저장
			localStorage.setItem('activeAdminTab', target);
		});
	});

	/* ==========================================
	   사용자 역할 변경
	========================================== */
	const updateRoleButtons = document.querySelectorAll('.update-role-btn');

	updateRoleButtons.forEach((button) => {
		button.addEventListener('click', () => {
			const row = button.closest('tr');
			const select = row.querySelector('.role-select');
			const userId = select.dataset.userId;
			const newRole = select.value;

			// 탈퇴한 회원 체크
			const statusCell = row.querySelector('td:nth-child(6)');
			if (statusCell && statusCell.textContent.trim() === '탈퇴') {
				alert('탈퇴한 회원은 역할을 변경할 수 없습니다.');
				return;
			}

			if (!userId || !newRole) {
				alert('유효하지 않은 데이터입니다.');
				return;
			}

			sendUpdateRequest(
				`/admin/update-role?id=${userId}&role=${newRole}`,
				'해당 사용자의 역할을 변경하시겠습니까?',
				'역할이 성공적으로 수정되었습니다.'
			);
		});
	});

	/* ==========================================
	   식당 오너 지정
	========================================== */
	const updateOwnerButtons = document.querySelectorAll('.update-owner-btn');

	updateOwnerButtons.forEach((button) => {
		button.addEventListener('click', () => {
			const row = button.closest('tr');
			const select = row.querySelector('.owner-select');
			const restaurantId = select.dataset.restaurantId;
			const ownerId = select.value;

			if (!restaurantId) {
				alert('식당 ID를 찾을 수 없습니다.');
				return;
			}

			sendUpdateRequest(
				`/admin/update-owner?restaurantId=${restaurantId}&ownerId=${ownerId}`,
				'선택한 오너로 식당을 지정하시겠습니까?',
				'식당 오너가 성공적으로 지정되었습니다.'
			);
		});
	});

	/* ==========================================
	   답글 모달 초기화
	========================================== */
	initializeReplyButtons();
	initializeModalCloseEvent();
	initializeReplyFormSubmit();

});

/* ==========================================
   답글 버튼 초기화
========================================== */
const initializeReplyButtons = () => {
	const replyButtons = document.querySelectorAll('.reply-btn');

	replyButtons.forEach((btn) => {
		btn.addEventListener('click', () => {
			const postId = btn.getAttribute('data-post-id');
			const postTitle = btn.getAttribute('data-post-title');
			openReplyModal(postId, postTitle);
		});
	});
};

/* ==========================================
   답글 모달 열기
========================================== */
const openReplyModal = (postId, postTitle) => {
	// 부모 게시글 ID 설정
	document.getElementById('reply-parent-id').value = postId;

	// 제목 자동 입력 (Re: 접두사)
	document.getElementById('reply-title').value = 'Re: ' + postTitle;

	// 내용 초기화
	document.getElementById('reply-content').value = '';

	// 모달 표시
	const modal = document.getElementById('reply-modal');
	modal.style.display = 'block';

	// 모달 중앙 정렬
	centerModal(modal);

	// 제목 입력란에 포커스
	document.getElementById('reply-title').focus();
};

/* ==========================================
   모달 중앙 정렬
========================================== */
const centerModal = (modal) => {
	const modalContent = modal.querySelector('.modal-content');

	const windowHeight = window.innerHeight;
	const windowWidth = window.innerWidth;

	const modalHeight = modalContent.offsetHeight;
	const modalWidth = modalContent.offsetWidth;

	// 중앙 위치 계산
	const leftPosition = (windowWidth - modalWidth) / 2;
	const topPosition = (windowHeight - modalHeight) / 2 - 100;

	modalContent.style.position = 'fixed';
	modalContent.style.left = leftPosition + 'px';
	modalContent.style.top = Math.max(topPosition, 50) + 'px';
	modalContent.style.zIndex = '1001';
};

/* ==========================================
   답글 모달 닫기
========================================== */
const closeReplyModal = () => {
	document.getElementById('reply-modal').style.display = 'none';

	// 모달 스타일 초기화
	const modalContent = document.getElementById('reply-modal').querySelector('.modal-content');
	modalContent.style.position = '';
	modalContent.style.left = '';
	modalContent.style.top = '';
	modalContent.style.zIndex = '';

	// 폼 초기화
	document.getElementById('reply-form').reset();
};

/* ==========================================
   모달 외부 클릭 시 닫기
========================================== */
const initializeModalCloseEvent = () => {
	const modal = document.getElementById('reply-modal');

	window.addEventListener('click', (event) => {
		if (event.target === modal) {
			closeReplyModal();
		}
	});
};

/* ==========================================
   답글 폼 제출 검증
========================================== */
const initializeReplyFormSubmit = () => {
	const replyForm = document.getElementById('reply-form');

	if (replyForm) {
		replyForm.addEventListener('submit', (event) => {
			const parentId = document.getElementById('reply-parent-id').value;
			const title = document.getElementById('reply-title').value;
			const content = document.getElementById('reply-content').value;

			// 유효성 검사
			if (!parentId || !title.trim() || !content.trim()) {
				alert('제목과 내용을 모두 입력해주세요.');
				event.preventDefault();
				return;
			}
		});
	}
};

/* ==========================================
   ESC 키로 모달 닫기
========================================== */
document.addEventListener('keydown', (event) => {
	if (event.key === 'Escape') {
		const modal = document.getElementById('reply-modal');
		if (modal && modal.style.display === 'block') {
			closeReplyModal();
		}
	}
});

/* ==========================================
   창 크기 변경 시 모달 재정렬
========================================== */
window.addEventListener('resize', () => {
	const modal = document.getElementById('reply-modal');
	if (modal && modal.style.display === 'block') {
		centerModal(modal);
	}
});
