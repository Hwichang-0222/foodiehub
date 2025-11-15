/* ============================================
   AI 추천 설문 - JavaScript
============================================ */

let currentStep = 1;
let regionData = null;

// -------------------------------------------------------------
// 페이지 로드 시 전체 초기화
// -------------------------------------------------------------
document.addEventListener("DOMContentLoaded", async function () {

    /* -------------------------------
       0. 로그인 사용자 자동 입력
    --------------------------------*/
    const loginGender = document.getElementById("loginGender")?.value;
    const loginBirth = document.getElementById("loginBirth")?.value;
    const loginRegion1 = document.getElementById("loginRegion1")?.value;
    const loginRegion2 = document.getElementById("loginRegion2")?.value;

    // (1) 성별 자동 체크
    if (loginGender) {
        const genderRadio = document.querySelector(`input[name="gender"][value="${loginGender}"]`);
        if (genderRadio) genderRadio.checked = true;
    }

    // (2) 나이 → 연령대 자동 선택
    if (loginBirth) {
        const birthYear = parseInt(loginBirth.substring(0, 4));
        const age = new Date().getFullYear() - birthYear;
        let group = "";

        if (age < 20) group = "10s";
        else if (age < 30) group = "20s";
        else if (age < 40) group = "30s";
        else if (age < 50) group = "40s";
        else group = "50s_plus";

        const target = document.querySelector(`input[name="ageGroup"][value="${group}"]`);
        if (target) target.checked = true;
    }

    // (3) 시/도 + 구 자동 선택을 위해 JSON 먼저 로드
    await loadRegionData();

    if (loginRegion1) {
        document.getElementById("regionLevel1").value = loginRegion1;
        fillDistricts(loginRegion1, loginRegion2);
    }

    document.getElementById("regionLevel1").addEventListener("change", e => {
        fillDistricts(e.target.value, "");
    });
});


// -------------------------------------------------------------
// 행정구역 JSON 로드
// -------------------------------------------------------------
async function loadRegionData() {
    try {
        const response = await fetch('/data/regions.json');
        regionData = await response.json();
        populateRegionSelect();
    } catch (error) {
        console.error("행정구역 데이터 로드 실패:", error);
    }
}

// -------------------------------------------------------------
// 시/도 목록 채우기
// -------------------------------------------------------------
function populateRegionSelect() {
    const regionSelect = document.getElementById('regionLevel1');
    regionSelect.innerHTML = '<option value="">시/도 선택</option>';

    Object.keys(regionData).forEach(region => {
        const option = document.createElement('option');
        option.value = region;
        option.textContent = region;
        regionSelect.appendChild(option);
    });
}

// -------------------------------------------------------------
// 특정 시/도 선택 → 구 목록 채우기
// -------------------------------------------------------------
function fillDistricts(region, selectedDistrict = "") {
    const districtSelect = document.getElementById("regionLevel2");
    districtSelect.innerHTML = '<option value="">시/군/구 선택</option>';

    if (!region || !regionData[region]) return;

    regionData[region].forEach(d => {
        const option = document.createElement("option");
        option.value = d;
        option.textContent = d;
        districtSelect.appendChild(option);
    });

    if (selectedDistrict) districtSelect.value = selectedDistrict;
}


// -------------------------------------------------------------
// 설문 단계 이동
// -------------------------------------------------------------
function goToStep(stepNumber) {
    if (stepNumber > currentStep && !validateCurrentStep()) return;

    const currentSection = document.getElementById(`step${currentStep}`);
    const nextSection = document.getElementById(`step${stepNumber}`);

    if (stepNumber > currentStep) {
        currentSection.classList.add('next-exit');
    } else {
        currentSection.classList.add('prev-exit');
        nextSection.style.animation = "slideInLeft 0.5s ease";
    }

    currentSection.classList.remove("active");

    setTimeout(() => {
        currentSection.classList.remove('next-exit', 'prev-exit');
        nextSection.classList.add("active");
        currentStep = stepNumber;
        updateProgressBar();
    }, 200);
}

// -------------------------------------------------------------
// 진행바 업데이트
// -------------------------------------------------------------
function updateProgressBar() {
    const progressFill = document.getElementById("progressFill");
    const stepIndicator = document.getElementById("currentStep");
    progressFill.style.width = `${(currentStep / 4) * 100}%`;
    stepIndicator.textContent = currentStep;
}

// -------------------------------------------------------------
// 단계 유효성 검사
// -------------------------------------------------------------
function validateCurrentStep() {
    if (currentStep === 1) {
        if (!document.querySelector('input[name="gender"]:checked')) return alert("성별을 선택해주세요."), false;
        if (!document.querySelector('input[name="ageGroup"]:checked')) return alert("연령대를 선택해주세요."), false;
        if (!document.getElementById("regionLevel1").value) return alert("지역을 선택해주세요."), false;
        if (!document.getElementById("regionLevel2").value) return alert("상세 지역을 선택해주세요."), false;
    }
    if (currentStep === 2 && !document.querySelector('input[name="foodType"]:checked')) return alert("음식 종류를 선택해주세요."), false;
    if (currentStep === 3 && !document.querySelector('input[name="mood"]:checked')) return alert("현재 기분을 선택해주세요."), false;
    if (currentStep === 4 && !document.querySelector('input[name="withWho"]:checked')) return alert("인원을 선택해주세요."), false;

    return true;
}

// -------------------------------------------------------------
// 설문 제출
// -------------------------------------------------------------
function submitSurvey() {
    if (!validateCurrentStep()) return;

    document.getElementById("loadingOverlay").classList.add("active");

    document.getElementById("hiddenGender").value = document.querySelector('input[name="gender"]:checked').value;
    document.getElementById("hiddenAgeGroup").value = document.querySelector('input[name="ageGroup"]:checked').value;
    document.getElementById("hiddenRegion").value = document.getElementById("regionLevel1").value;
    document.getElementById("hiddenDistrict").value = document.getElementById("regionLevel2").value;
    document.getElementById("hiddenCraving").value = document.querySelector('input[name="foodType"]:checked').value;
    document.getElementById("hiddenMood").value = document.querySelector('input[name="mood"]:checked').value;
    document.getElementById("hiddenWithWho").value = document.querySelector('input[name="withWho"]:checked').value;

    document.getElementById("surveyForm").submit();
}
