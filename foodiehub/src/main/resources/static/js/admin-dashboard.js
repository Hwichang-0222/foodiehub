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

});