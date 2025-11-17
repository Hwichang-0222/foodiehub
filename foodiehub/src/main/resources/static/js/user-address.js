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
// 기존 주소를 분해해서 필드에 채우기 (회원정보 수정용)
function parseExistingAddress() {
    const addressField = document.getElementById("address");
    
    if (!addressField) {
        console.log("address 필드를 찾을 수 없습니다.");
        return;
    }
    
    const fullAddress = addressField.value;
    
    console.log("기존 주소:", fullAddress);
    
    if (!fullAddress) {
        console.log("주소 값이 비어있습니다.");
        return;
    }

    // 정규식: (우편번호) 도로명주소 [상세주소]
    // 우편번호와 나머지를 분리하고, 나머지는 roadAddr에 모두 넣기
    const match = fullAddress.match(/^\((\d{5})\)\s*(.+)$/);

    if (match) {
        const [, zipcode, restAddress] = match;

        const baseAddressField = document.getElementById("baseAddress");
        const roadAddrField = document.getElementById("roadAddr");
        
        if (baseAddressField) baseAddressField.value = zipcode || "";
        if (roadAddrField) roadAddrField.value = restAddress || "";
        
        console.log("주소 파싱 성공:", { zipcode, restAddress });
    } else {
        // 정규식 매칭 실패 시 전체 주소를 roadAddr에 넣기
        const roadAddrField = document.getElementById("roadAddr");
        if (roadAddrField) roadAddrField.value = fullAddress;
        console.log("정규식 매칭 실패, 전체 주소를 roadAddr에 입력:", fullAddress);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const signupForm = document.getElementById("signupForm");
    if (signupForm) signupForm.addEventListener("submit", assembleFullAddress);

    const updateForm = document.getElementById("updateForm");
    if (updateForm) updateForm.addEventListener("submit", assembleFullAddress);

    // 회원정보 수정 페이지에서 기존 주소 파싱
    parseExistingAddress();
});