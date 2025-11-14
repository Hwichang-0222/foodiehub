/* ============================================
   FoodieHub – 지역 필터 로직 (JSON 기반 자동 로딩)
============================================ */

let regionsData = {};   // JSON에서 불러온 데이터 저장

/* ----- 1. Thymeleaf 변수 ---- */
let currentSelectedCity = window.regionLevel1 || null;
let currentSelectedDistrict = window.regionLevel2 || null;
let currentCategory = window.category || null;
let currentKeyword = window.keyword || null;

[currentSelectedCity, currentSelectedDistrict, currentCategory, currentKeyword] =
    [currentSelectedCity, currentSelectedDistrict, currentCategory, currentKeyword].map(v => v === '' ? null : v);

/* ============================================
   2) JSON 파일 로드
============================================ */
document.addEventListener('DOMContentLoaded', async function() {

    try {
        const res = await fetch('/data/regions.json');
        regionsData = await res.json();
        console.log("지역 데이터 로딩 완료:", regionsData);
    } catch (e) {
        console.error("지역 데이터 로딩 실패:", e);
        return;
    }

    const box = document.getElementById('district-filter-box');
    const body = document.getElementById('district-filter-body');

    box.style.display = 'block';

    // 선택된 도시가 없는 상태 → 안내문 표시
    if (!currentSelectedCity || currentSelectedCity === '기타') {
        body.innerHTML = '<p style="grid-column:1 / -1; color:#777;">지역을 먼저 선택하세요</p>';
        return;
    }

    // 선택된 도시가 존재하면 상세 지역 로딩
    showDistrictFilter(currentSelectedCity, false);
});

/* ============================================
   3) 도시 선택 시 상세지역 로드
============================================ */
function selectCity(city) {
    currentSelectedCity = city;

    if (city === '기타') {
        window.location.href = buildURL(currentCategory, city, null, currentKeyword);
        return;
    }

    showDistrictFilter(city, true);
}

/* ============================================
   4) 상세 지역 생성
============================================ */
function showDistrictFilter(city, move) {
    const box = document.getElementById('district-filter-box');
    const body = document.getElementById('district-filter-body');

    box.style.display = 'block';
    body.innerHTML = "";

    let districts = regionsData[city];

    // JSON에 도시 키가 없을 때
    if (!districts) {
        body.innerHTML = '<p style="grid-column:1 / -1; color:#777;">상세 지역 없음</p>';
        return;
    }

    // 전체 버튼
    const all = document.createElement('a');
    all.href = buildURL(currentCategory, city, null, currentKeyword);
    all.textContent = '전체';
    if (!currentSelectedDistrict) all.classList.add('active');
    body.appendChild(all);

    // 각 구/군 생성
    districts.forEach(d => {
        const link = document.createElement('a');
        link.href = buildURL(currentCategory, city, d, currentKeyword);
        link.textContent = d;
        if (currentSelectedDistrict === d) link.classList.add('active');
        body.appendChild(link);
    });

    // 도시 선택 후 자동 이동
    if (move) {
        window.location.href = buildURL(currentCategory, city, null, currentKeyword);
    }
}

/* ============================================
   5) 상세지역 초기화 (전체)
============================================ */
function clearDistrict() {
    window.location.href = buildURL(currentCategory, currentSelectedCity, null, currentKeyword);
}

/* ============================================
   6) URL 빌더
============================================ */
function buildURL(category, regionLevel1, regionLevel2, keyword) {
    const params = new URLSearchParams();

    if (category) params.append('category', category);
    if (regionLevel1) params.append('regionLevel1', regionLevel1);
    if (regionLevel2) params.append('regionLevel2', regionLevel2);
    if (keyword) params.append('keyword', keyword);

    return '/restaurant/list' + (params.toString() ? '?' + params.toString() : '');
}
