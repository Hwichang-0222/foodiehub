document.addEventListener("DOMContentLoaded", () => {

    /* ------------------------------
        공통 함수
    ------------------------------ */
    // API 요청 공통 함수
    function sendUpdateRequest(url, confirmMsg, successMsg) {
        if (confirm(confirmMsg)) {
            fetch(url, { method: "GET" })
                .then(response => {
                    if (response.ok) {
                        alert(successMsg);
                        location.reload();
                    } else {
                        alert("요청 처리 중 오류가 발생했습니다.");
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert("서버 요청 중 오류가 발생했습니다.");
                });
        }
    }

    /* ------------------------------
        1. 탭 전환 기능
    ------------------------------ */
    const tabs = document.querySelectorAll(".admin-tab");
    const contents = document.querySelectorAll(".tab-content");

    // 마지막 활성 탭 복원
    const savedTab = localStorage.getItem("activeAdminTab");
    if (savedTab) {
        contents.forEach(c => c.classList.remove("active"));
        tabs.forEach(t => t.classList.remove("active"));

        const targetTab = document.querySelector(savedTab);
        if (targetTab) {
            targetTab.classList.add("active");
            const btn = document.querySelector(`[data-target="${savedTab}"]`);
            if (btn) btn.classList.add("active");
        }
    }

    // 탭 클릭 시 동작
    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            const target = tab.dataset.target;

            tabs.forEach(t => t.classList.remove("active"));
            contents.forEach(c => c.classList.remove("active"));

            tab.classList.add("active");
            document.querySelector(target).classList.add("active");

            localStorage.setItem("activeAdminTab", target);
        });
    });

    /* ------------------------------
        2. 유저 역할(Role) 수정
    ------------------------------ */
    const updateRoleButtons = document.querySelectorAll(".update-role-btn");

    updateRoleButtons.forEach(button => {
        button.addEventListener("click", () => {
            const row = button.closest("tr");
            const select = row.querySelector(".role-select");
            const userId = select.dataset.userId;
            const newRole = select.value;

            // 탈퇴 회원 체크
            const statusCell = row.querySelector("td:nth-child(6)");
            if (statusCell && statusCell.textContent.trim() === "탈퇴") {
                alert("탈퇴한 회원은 역할을 변경할 수 없습니다.");
                return;
            }

            if (!userId || !newRole) {
                alert("유효하지 않은 데이터입니다.");
                return;
            }

            sendUpdateRequest(
                `/admin/update-role?id=${userId}&role=${newRole}`,
                "해당 사용자의 역할을 변경하시겠습니까?",
                "역할이 성공적으로 수정되었습니다."
            );
        });
    });

    /* ------------------------------
        3. 식당 오너 지정
    ------------------------------ */
    const updateOwnerButtons = document.querySelectorAll(".update-owner-btn");

    updateOwnerButtons.forEach(button => {
        button.addEventListener("click", () => {
            const row = button.closest("tr");
            const select = row.querySelector(".owner-select");
            const restaurantId = select.dataset.restaurantId;
            const ownerId = select.value;

            if (!restaurantId) {
                alert("식당 ID를 찾을 수 없습니다.");
                return;
            }

            sendUpdateRequest(
                `/admin/update-owner?restaurantId=${restaurantId}&ownerId=${ownerId}`,
                "선택한 오너로 식당을 지정하시겠습니까?",
                "식당 오너가 성공적으로 지정되었습니다."
            );
        });
    });

    /* ------------------------------
        2-1. 답글 버튼 초기화 - 페이지 로드 시 이벤트 리스너 등록
    ------------------------------ */
    // 모든 답글 버튼에 클릭 이벤트 등록
    initializeReplyButtons();
    // 모달 외부 클릭 시 닫기 이벤트 등록
    initializeModalCloseEvent();
    // 답글 폼 제출 이벤트 등록
    initializeReplyFormSubmit();

});

// 1. 답글 버튼에 이벤트 리스너 등록
function initializeReplyButtons() {
	// 답글 버튼 찾기
	const replyButtons = document.querySelectorAll('.reply-btn');
	
	// 각 버튼에 클릭 이벤트 등록
	replyButtons.forEach(function(btn) {
		btn.addEventListener('click', function() {
			// 부모글 ID와 제목 가져오기
			const postId = this.getAttribute('data-post-id');
			const postTitle = this.getAttribute('data-post-title');
			// 모달 열기
			openReplyModal(postId, postTitle);
		});
	});
}

// 2. 답글 모달 열기 (화면 정중앙 위치)
function openReplyModal(postId, postTitle) {
	// 부모글 ID 설정 (숨김 필드)
	document.getElementById('reply-parent-id').value = postId;
	
	// 제목 자동 입력: "Re: " + 원글제목
	document.getElementById('reply-title').value = 'Re: ' + postTitle;
	
	// 내용 초기화 (빈칸)
	document.getElementById('reply-content').value = '';
	
	// 모달 표시
	const modal = document.getElementById('reply-modal');
	modal.style.display = 'block';
	
	// 모달을 화면 정중앙에 위치
	centerModal(modal);
	
	// 제목 필드에 포커스 설정
	document.getElementById('reply-title').focus();
}

// 2-1. 모달을 화면 정중앙에 위치시키는 함수
function centerModal(modal) {
	// 모달 콘텐츠 찾기
	const modalContent = modal.querySelector('.modal-content');
	
	// 윈도우 크기 계산
	const windowHeight = window.innerHeight;
	const windowWidth = window.innerWidth;
	
	// 모달 콘텐츠의 크기
	const modalHeight = modalContent.offsetHeight;
	const modalWidth = modalContent.offsetWidth;
	
	// 수평 정중앙 계산
	const leftPosition = (windowWidth - modalWidth) / 2;
	// 수직 정중앙 계산 (약간 위쪽에 위치)
	const topPosition = (windowHeight - modalHeight) / 2 - 100;
	
	// 모달 콘텐츠의 위치 설정
	modalContent.style.position = 'fixed';
	modalContent.style.left = leftPosition + 'px';
	modalContent.style.top = Math.max(topPosition, 50) + 'px';
	modalContent.style.zIndex = '1001';
}

// 3. 답글 모달 닫기
function closeReplyModal() {
	// 모달 숨기기
	document.getElementById('reply-modal').style.display = 'none';
	
	// 모달 콘텐츠 위치 초기화
	const modalContent = document.getElementById('reply-modal').querySelector('.modal-content');
	modalContent.style.position = '';
	modalContent.style.left = '';
	modalContent.style.top = '';
	modalContent.style.zIndex = '';
	
	// 폼 초기화
	document.getElementById('reply-form').reset();
}

// 4. 모달 외부 클릭 시 닫기
function initializeModalCloseEvent() {
	// 모달 엘리먼트 찾기
	const modal = document.getElementById('reply-modal');
	
	// 윈도우 클릭 이벤트 등록
	window.addEventListener('click', function(event) {
		// 모달 외부를 클릭했는지 확인
		if (event.target === modal) {
			// 모달 닫기
			closeReplyModal();
		}
	});
}

// 5. 답글 폼 제출 이벤트 등록
function initializeReplyFormSubmit() {
	const replyForm = document.getElementById('reply-form');
	
	if (replyForm) {
		replyForm.addEventListener('submit', function(event) {
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
}

// 6. ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
	// 모달이 표시 중일 때 ESC 키로 닫기
	if (event.key === 'Escape') {
		const modal = document.getElementById('reply-modal');
		if (modal && modal.style.display === 'block') {
			closeReplyModal();
		}
	}
});

// 7. 윈도우 리사이즈 시 모달 위치 조정
window.addEventListener('resize', function() {
	// 모달이 표시 중일 때만 위치 조정
	const modal = document.getElementById('reply-modal');
	if (modal && modal.style.display === 'block') {
		centerModal(modal);
	}
});