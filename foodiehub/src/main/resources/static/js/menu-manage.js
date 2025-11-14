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
   메뉴 수정 모달 열기 
-------------------------------------- */
function openEditMenu(menuId) {
    // 기존 모달이 있으면 제거
    const existingModal = document.getElementById('editMenuModal');
    if (existingModal) {
        existingModal.remove();
    }

    // 행에서 데이터 가져오기
    const row = document.getElementById(`menuRow-${menuId}`);
    const name = row.dataset.menuName;
    const price = row.dataset.menuPrice;
    const description = row.dataset.menuDescription || '';

    // 모달 생성
    const modal = document.createElement('div');
    modal.id = 'editMenuModal';
    modal.className = 'modal-overlay';
    
    modal.innerHTML = `
        <div class="modal-content">
            <h3>메뉴 수정</h3>
            <form id="editMenuForm">
                <input type="hidden" name="id" value="${menuId}">
                <input type="hidden" name="restaurantId" value="${document.querySelector('input[name="restaurantId"]').value}">
                
                <label>메뉴명</label>
                <input type="text" name="name" value="${name}" required>
                
                <label>설명</label>
                <textarea name="description" rows="3">${description}</textarea>
                
                <label>가격</label>
                <input type="number" name="price" value="${price}" required>
                
                <div class="modal-buttons">
                    <button type="submit" class="btn btn-create">수정</button>
                    <button type="button" class="btn btn-danger" onclick="closeEditModal()">취소</button>
                </div>
            </form>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // 모달 외부 클릭 시 닫기
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            closeEditModal();
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
            
            closeEditModal();
            alert('메뉴가 수정되었습니다.');
        } else {
            alert('수정에 실패했습니다.');
        }
    });
}

// 모달 닫기
function closeEditModal() {
    const modal = document.getElementById('editMenuModal');
    if (modal) {
        modal.remove();
    }
}


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
   이미지 모달 (크게 보기)
-------------------------------------- */
function openImageModal(src) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.style.cursor = 'pointer';
    
    modal.innerHTML = `
        <div style="max-width: 90%; max-height: 90%; overflow: auto;">
            <img src="${src}" style="width: 100%; height: auto; display: block;">
        </div>
    `;
    
    modal.addEventListener('click', function() {
        modal.remove();
    });
    
    document.body.appendChild(modal);
}