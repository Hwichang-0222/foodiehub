document.addEventListener('DOMContentLoaded', () => {

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

	const tabs = document.querySelectorAll('.admin-tab');
	const contents = document.querySelectorAll('.tab-content');

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

	tabs.forEach((tab) => {
		tab.addEventListener('click', () => {
			const target = tab.dataset.target;

			tabs.forEach((t) => t.classList.remove('active'));
			contents.forEach((c) => c.classList.remove('active'));

			tab.classList.add('active');
			document.querySelector(target).classList.add('active');

			localStorage.setItem('activeAdminTab', target);
		});
	});

	const updateRoleButtons = document.querySelectorAll('.update-role-btn');

	updateRoleButtons.forEach((button) => {
		button.addEventListener('click', () => {
			const row = button.closest('tr');
			const select = row.querySelector('.role-select');
			const userId = select.dataset.userId;
			const newRole = select.value;

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

	initializeReplyButtons();
	initializeModalCloseEvent();
	initializeReplyFormSubmit();

});

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

const openReplyModal = (postId, postTitle) => {
	document.getElementById('reply-parent-id').value = postId;

	document.getElementById('reply-title').value = 'Re: ' + postTitle;

	document.getElementById('reply-content').value = '';

	const modal = document.getElementById('reply-modal');
	modal.style.display = 'block';

	centerModal(modal);

	document.getElementById('reply-title').focus();
};

const centerModal = (modal) => {
	const modalContent = modal.querySelector('.modal-content');

	const windowHeight = window.innerHeight;
	const windowWidth = window.innerWidth;

	const modalHeight = modalContent.offsetHeight;
	const modalWidth = modalContent.offsetWidth;

	const leftPosition = (windowWidth - modalWidth) / 2;
	const topPosition = (windowHeight - modalHeight) / 2 - 100;

	modalContent.style.position = 'fixed';
	modalContent.style.left = leftPosition + 'px';
	modalContent.style.top = Math.max(topPosition, 50) + 'px';
	modalContent.style.zIndex = '1001';
};

const closeReplyModal = () => {
	document.getElementById('reply-modal').style.display = 'none';

	const modalContent = document.getElementById('reply-modal').querySelector('.modal-content');
	modalContent.style.position = '';
	modalContent.style.left = '';
	modalContent.style.top = '';
	modalContent.style.zIndex = '';

	document.getElementById('reply-form').reset();
};

const initializeModalCloseEvent = () => {
	const modal = document.getElementById('reply-modal');

	window.addEventListener('click', (event) => {
		if (event.target === modal) {
			closeReplyModal();
		}
	});
};

const initializeReplyFormSubmit = () => {
	const replyForm = document.getElementById('reply-form');

	if (replyForm) {
		replyForm.addEventListener('submit', (event) => {
			const parentId = document.getElementById('reply-parent-id').value;
			const title = document.getElementById('reply-title').value;
			const content = document.getElementById('reply-content').value;

			if (!parentId || !title.trim() || !content.trim()) {
				alert('제목과 내용을 모두 입력해주세요.');
				event.preventDefault();
				return;
			}
		});
	}
};

document.addEventListener('keydown', (event) => {
	if (event.key === 'Escape') {
		const modal = document.getElementById('reply-modal');
		if (modal && modal.style.display === 'block') {
			closeReplyModal();
		}
	}
});

window.addEventListener('resize', () => {
	const modal = document.getElementById('reply-modal');
	if (modal && modal.style.display === 'block') {
		centerModal(modal);
	}
});
