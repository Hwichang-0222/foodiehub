/* --------------------------------------
   메뉴 관리 완료 후 이동
-------------------------------------- */
function completeMenuManage() {
    const userRole = document.getElementById('userRole').value;
    const restaurantId = document.getElementById('restaurantId').value;
    
    // 역할에 따라 다른 페이지로 이동
    if (userRole === 'ADMIN') {
        // 관리자 → 관리자 대시보드
        window.location.href = '/admin/dashboard';
    } else if (userRole === 'OWNER') {
        // 식당 사장 → 해당 식당 상세 페이지
        window.location.href = '/restaurant/detail/' + restaurantId;
    } else {
        // 기타 → 메인 페이지
        window.location.href = '/';
    }
}


/* --------------------------------------
   메뉴 추가 AJAX 
-------------------------------------- */
document.getElementById("addMenuForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const formData = new FormData(this);

    const res = await fetch("/menu/add", {
        method: "POST",
        body: formData
    });

    const menu = await res.json();

    appendMenuToTable(menu);
    this.reset();
});

// 테이블 즉시 추가
function appendMenuToTable(menu) {
    const tbody = document.getElementById("menuTableBody");

    const tr = document.createElement("tr");
    tr.setAttribute("id", `menuRow-${menu.id}`);
    
    // data 속성 추가
    tr.dataset.menuId = menu.id;
    tr.dataset.menuName = menu.name;
    tr.dataset.menuPrice = menu.price;
    tr.dataset.menuDescription = menu.description || '';

    tr.innerHTML = `
        <td>${menu.name}</td>
        <td>${menu.description || ''}</td>
        <td>${menu.price}</td>
        <td><button onclick="openEditMenu(${menu.id})" class="btn">수정</button></td>
        <td><button onclick="deleteMenu(${menu.id})" class="btn btn-danger">삭제</button></td>
    `;

    tbody.appendChild(tr);
}


/* --------------------------------------
   메뉴 수정 모달 열기 (HTML 모달 활용)
-------------------------------------- */
function openEditMenu(menuId) {
    // 행에서 데이터 가져오기
    const row = document.getElementById(`menuRow-${menuId}`);
    const name = row.dataset.menuName;
    const price = row.dataset.menuPrice;
    const description = row.dataset.menuDescription || '';

    // 모달 폼에 데이터 채우기
    document.getElementById('editMenuId').value = menuId;
    document.getElementById('editMenuName').value = name;
    document.getElementById('editMenuPrice').value = price;
    document.getElementById('editMenuDescription').value = description;

    // 모달 열기
    const modal = document.getElementById('editMenuModal');
    modal.classList.add('active');
}


/* --------------------------------------
   메뉴 수정 모달 이벤트 (DOMContentLoaded)
-------------------------------------- */
document.addEventListener('DOMContentLoaded', function() {
    
    // 메뉴 수정 모달
    const editModal = document.getElementById('editMenuModal');
    const editModalClose = editModal.querySelector('.modal-close');
    const editModalCancel = editModal.querySelector('.btn-cancel');
    
    // 모달 닫기 버튼
    editModalClose.addEventListener('click', function() {
        editModal.classList.remove('active');
    });
    
    // 모달 취소 버튼
    editModalCancel.addEventListener('click', function() {
        editModal.classList.remove('active');
    });
    
    // 모달 외부 클릭 시 닫기
    editModal.addEventListener('click', function(e) {
        if (e.target === editModal) {
            editModal.classList.remove('active');
        }
    });
    
    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && editModal.classList.contains('active')) {
            editModal.classList.remove('active');
        }
    });
    
    // 수정 폼 제출
    document.getElementById('editMenuForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        
        const res = await fetch('/menu/update', {
            method: 'POST',
            body: formData
        });
        
        if (res.ok) {
            const menu = await res.json();
            
            // 테이블 행 업데이트
            const row = document.getElementById(`menuRow-${menu.id}`);
            row.cells[0].textContent = menu.name;
            row.cells[1].textContent = menu.description || '';
            row.cells[2].textContent = menu.price;
            
            // data 속성도 업데이트
            row.dataset.menuName = menu.name;
            row.dataset.menuPrice = menu.price;
            row.dataset.menuDescription = menu.description || '';
            
            editModal.classList.remove('active');
            alert('메뉴가 수정되었습니다.');
        } else {
            alert('수정에 실패했습니다.');
        }
    });

    // 이미지 미리보기 모달
    const imageModal = document.getElementById('imagePreviewModal');
    const imageModalClose = imageModal.querySelector('.modal-close');
    
    // 이미지 모달 닫기
    imageModalClose.addEventListener('click', function() {
        imageModal.classList.remove('active');
    });
    
    // 이미지 모달 외부 클릭 시 닫기
    imageModal.addEventListener('click', function(e) {
        if (e.target === imageModal || e.target.closest('.modal-content')) {
            imageModal.classList.remove('active');
        }
    });
});


/* --------------------------------------
   메뉴 삭제 AJAX
-------------------------------------- */
async function deleteMenu(menuId) {
    if (!confirm("정말 삭제할까요?")) return;

    const res = await fetch(`/menu/delete/${menuId}`, { method: "POST" });

    if (res.ok) {
        document.getElementById(`menuRow-${menuId}`).remove();
        alert('메뉴가 삭제되었습니다.');
    } else {
        alert("삭제 실패");
    }
}


/* --------------------------------------
   메뉴판 이미지 업로드 미리보기
-------------------------------------- */
function previewMenuImage(e) {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = e => {
        document.getElementById("menuImagePreview").src = e.target.result;
    };
    reader.readAsDataURL(file);
}


/* --------------------------------------
   메뉴판 이미지 업로드 AJAX
-------------------------------------- */
document.getElementById("menuImageUploadForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const formData = new FormData(this);

    const res = await fetch("/menu/image/add", {
        method: "POST",
        body: formData
    });

    const data = await res.json();

    if (!data) {
        alert("이미지 업로드 실패");
        return;
    }

    appendMenuImage(data);
    this.reset();
    document.getElementById("menuImagePreview").src = "";
    alert('이미지가 추가되었습니다.');
});


/* --------------------------------------
   업로드된 이미지 즉시 추가
-------------------------------------- */
function appendMenuImage(img) {
    const list = document.querySelector(".menu-image-list");

    //  div wrapper 구조로 변경
    const wrapper = document.createElement("div");
    wrapper.setAttribute("id", `menuImg-${img.id}`);
    wrapper.classList.add("menu-image-wrapper");

    wrapper.innerHTML = `
        <img src="${img.imageUrl}" class="menu-image-thumb" onclick="openImageModal(this.src)" onerror="this.src='/images/default-menu-thumbnail.png'">
        <button class="btn btn-danger" onclick="deleteMenuImage(${img.id})">삭제</button>
    `;

    list.appendChild(wrapper);
}


/* --------------------------------------
   메뉴판 이미지 삭제
-------------------------------------- */
async function deleteMenuImage(id) {
    if (!confirm("이미지를 삭제할까요?")) return;

    const res = await fetch(`/menu/image/delete/${id}`, {
        method: "POST"
    });

    if (res.ok) {
        document.getElementById(`menuImg-${id}`).remove();
        alert('이미지가 삭제되었습니다.');
    } else {
        alert("삭제 실패");
    }
}


/* --------------------------------------
   이미지 모달 (크게 보기) - HTML 모달 활용
-------------------------------------- */
function openImageModal(src) {
    const modal = document.getElementById('imagePreviewModal');
    const img = document.getElementById('previewImage');
    
    img.src = src;
    modal.classList.add('active');
}