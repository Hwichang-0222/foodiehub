// 카카오 주소 API 로드
const script = document.createElement("script");
script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
document.head.appendChild(script);

// 주소 검색
function openAddressSearch() {
    new daum.Postcode({
        oncomplete: function(data) {

            const roadAddr = data.roadAddress || data.jibunAddress;

            // 1) 우편번호만 저장
            document.getElementById("baseAddress").value = data.zonecode;

            // 2) 도로명 주소 저장
            document.getElementById("roadAddr").value = roadAddr;

            // 3) 행정구역(level) 저장
            document.getElementById("region_level1").value = data.sido;
            document.getElementById("region_level2").value = data.sigungu;
            document.getElementById("region_level3").value = data.bname;

            // 4) 상세주소로 포커스 이동
            document.getElementById("detail").focus();

            // 추가: 주소 입력 후 검증 실행
            const changeEvent = new Event('change', { bubbles: true });
            document.getElementById("baseAddress").dispatchEvent(changeEvent);
            document.getElementById("roadAddr").dispatchEvent(changeEvent);
        }
    }).open();
}

// 최종 주소 조립 → hidden(address)에 저장
function assembleFullAddress() {
    const zip = document.getElementById("baseAddress").value;
    const road = document.getElementById("roadAddr").value;
    const detail = document.getElementById("detail").value;

    const fullAddress = `(${zip}) ${road} ${detail}`.trim();
    document.getElementById("address").value = fullAddress;
}

document.addEventListener("DOMContentLoaded", () => {
    const signupForm = document.getElementById("signupForm");
    if (signupForm) signupForm.addEventListener("submit", assembleFullAddress);

    const updateForm = document.getElementById("updateForm");
    if (updateForm) updateForm.addEventListener("submit", assembleFullAddress);
});