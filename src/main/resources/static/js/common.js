var finishURL = "http://www.chaimm.com/html/finish.html";
var appID = "wx82de919618ed3240";

/*
 * A JavaScript implementation of the Secure Hash Algorithm, SHA-1, as defined
 * in FIPS PUB 180-1
 * Version 2.1a Copyright Paul Johnston 2000 - 2002.
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for details.
 */

/*
 * Configurable variables. You may need to tweak these to be compatible with
 * the server-side, but the defaults work in most cases.
 */
var hexcase = 0;  /* hex output format. 0 - lowercase; 1 - uppercase        */
var b64pad  = ""; /* base-64 pad character. "=" for strict RFC compliance   */
var chrsz   = 8;  /* bits per input character. 8 - ASCII; 16 - Unicode      */

/*
 * These are the functions you'll usually want to call
 * They take string arguments and return either hex or base-64 encoded strings
 */
function hex_sha1(s){return binb2hex(core_sha1(str2binb(s),s.length * chrsz));}
function b64_sha1(s){return binb2b64(core_sha1(str2binb(s),s.length * chrsz));}
function str_sha1(s){return binb2str(core_sha1(str2binb(s),s.length * chrsz));}
function hex_hmac_sha1(key, data){ return binb2hex(core_hmac_sha1(key, data));}
function b64_hmac_sha1(key, data){ return binb2b64(core_hmac_sha1(key, data));}
function str_hmac_sha1(key, data){ return binb2str(core_hmac_sha1(key, data));}

/*
 * Perform a simple self-test to see if the VM is working
 */
function sha1_vm_test()
{
    return hex_sha1("abc") == "a9993e364706816aba3e25717850c26c9cd0d89d";
}

/*
 * Calculate the SHA-1 of an array of big-endian words, and a bit length
 */
function core_sha1(x, len)
{
    /* append padding */
    x[len >> 5] |= 0x80 << (24 - len % 32);
    x[((len + 64 >> 9) << 4) + 15] = len;

    var w = Array(80);
    var a =  1732584193;
    var b = -271733879;
    var c = -1732584194;
    var d =  271733878;
    var e = -1009589776;

    for(var i = 0; i < x.length; i += 16)
    {
        var olda = a;
        var oldb = b;
        var oldc = c;
        var oldd = d;
        var olde = e;

        for(var j = 0; j < 80; j++)
        {
            if(j < 16) w[j] = x[i + j];
            else w[j] = rol(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1);
            var t = safe_add(safe_add(rol(a, 5), sha1_ft(j, b, c, d)),
                safe_add(safe_add(e, w[j]), sha1_kt(j)));
            e = d;
            d = c;
            c = rol(b, 30);
            b = a;
            a = t;
        }

        a = safe_add(a, olda);
        b = safe_add(b, oldb);
        c = safe_add(c, oldc);
        d = safe_add(d, oldd);
        e = safe_add(e, olde);
    }
    return Array(a, b, c, d, e);

}

/*
 * Perform the appropriate triplet combination function for the current
 * iteration
 */
function sha1_ft(t, b, c, d)
{
    if(t < 20) return (b & c) | ((~b) & d);
    if(t < 40) return b ^ c ^ d;
    if(t < 60) return (b & c) | (b & d) | (c & d);
    return b ^ c ^ d;
}

/*
 * Determine the appropriate additive constant for the current iteration
 */
function sha1_kt(t)
{
    return (t < 20) ?  1518500249 : (t < 40) ?  1859775393 :
        (t < 60) ? -1894007588 : -899497514;
}

/*
 * Calculate the HMAC-SHA1 of a key and some data
 */
function core_hmac_sha1(key, data)
{
    var bkey = str2binb(key);
    if(bkey.length > 16) bkey = core_sha1(bkey, key.length * chrsz);

    var ipad = Array(16), opad = Array(16);
    for(var i = 0; i < 16; i++)
    {
        ipad[i] = bkey[i] ^ 0x36363636;
        opad[i] = bkey[i] ^ 0x5C5C5C5C;
    }

    var hash = core_sha1(ipad.concat(str2binb(data)), 512 + data.length * chrsz);
    return core_sha1(opad.concat(hash), 512 + 160);
}

/*
 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
 * to work around bugs in some JS interpreters.
 */
function safe_add(x, y)
{
    var lsw = (x & 0xFFFF) + (y & 0xFFFF);
    var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
    return (msw << 16) | (lsw & 0xFFFF);
}

/*
 * Bitwise rotate a 32-bit number to the left.
 */
function rol(num, cnt)
{
    return (num << cnt) | (num >>> (32 - cnt));
}

/*
 * Convert an 8-bit or 16-bit string to an array of big-endian words
 * In 8-bit function, characters >255 have their hi-byte silently ignored.
 */
function str2binb(str)
{
    var bin = Array();
    var mask = (1 << chrsz) - 1;
    for(var i = 0; i < str.length * chrsz; i += chrsz)
        bin[i>>5] |= (str.charCodeAt(i / chrsz) & mask) << (32 - chrsz - i%32);
    return bin;
}

/*
 * Convert an array of big-endian words to a string
 */
function binb2str(bin)
{
    var str = "";
    var mask = (1 << chrsz) - 1;
    for(var i = 0; i < bin.length * 32; i += chrsz)
        str += String.fromCharCode((bin[i>>5] >>> (32 - chrsz - i%32)) & mask);
    return str;
}

/*
 * Convert an array of big-endian words to a hex string.
 */
function binb2hex(binarray)
{
    var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
    var str = "";
    for(var i = 0; i < binarray.length * 4; i++)
    {
        str += hex_tab.charAt((binarray[i>>2] >> ((3 - i%4)*8+4)) & 0xF) +
            hex_tab.charAt((binarray[i>>2] >> ((3 - i%4)*8  )) & 0xF);
    }
    return str;
}

/*
 * Convert an array of big-endian words to a base-64 string
 */
function binb2b64(binarray)
{
    var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    var str = "";
    for(var i = 0; i < binarray.length * 4; i += 3)
    {
        var triplet = (((binarray[i   >> 2] >> 8 * (3 -  i   %4)) & 0xFF) << 16)
            | (((binarray[i+1 >> 2] >> 8 * (3 - (i+1)%4)) & 0xFF) << 8 )
            |  ((binarray[i+2 >> 2] >> 8 * (3 - (i+2)%4)) & 0xFF);
        for(var j = 0; j < 4; j++)
        {
            if(i * 8 + j * 6 > binarray.length * 32) str += b64pad;
            else str += tab.charAt((triplet >> 6*(3-j)) & 0x3F);
        }
    }
    return str;
}

/**
 * 지정한 길이의 랜덤 문자열을 생성한다
 */
function randomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';    /****혼동하기 쉬운 문자 oOLl,9gq,Vv,Uu,I1은 기본으로 제외했다****/
    var maxPos = $chars.length;
    var pwd = '';
    for (i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

function chooseImage(){
    wx.chooseImage({
        count: 1, // 기본값 9
        sizeType: ['original', 'compressed'], // 원본/압축 이미지 지정 가능, 기본은 둘 다
        sourceType: ['album', 'camera'], // 출처를 앨범/카메라로 지정 가능, 기본은 둘 다
        success: function (res) {
            var localIds = res.localIds; // 선택한 사진의 로컬 ID 목록을 반환한다. localId는 img 태그의 src 속성으로 이미지 표시에 쓸 수 있다
            debugger;
        }
    });
}


function upload() {
    // var xmlhttp;
    // if (window.XMLHttpRequest)
    // {// code for IE7+, Firefox, Chrome, Opera, Safari
    //     xmlhttp=new XMLHttpRequest();
    // }
    // else
    // {// code for IE6, IE5
    //     xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    // }
    // xmlhttp.onreadystatechange=function()
    // {
    //     if (xmlhttp.readyState==4 && xmlhttp.status==200)
    //     {
    //         var data = xmlhttp.responseText;
    //
    //         var nonceStr = randomString(16);
    //         var timestamp = Math.round(new Date().getTime() / 1000);
    //         var url = finishURL;
    //         var string1 = "jsapi_ticket=" + data + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
    //         var signature = hex_sha1(string1);
    //
    //         wx.config({
    //             debug: false, // 디버그 모드 활성화. 호출한 모든 api의 반환값이 클라이언트에 alert로 표시된다. 전달 파라미터를 보려면 PC에서 열면 되며, 파라미터 정보는 log로 출력되고 PC에서만 출력된다.
    //             appId: appID, // 필수, 공식 계정의 고유 식별자
    //             timestamp: timestamp, // 필수, 서명 생성 타임스탬프
    //             nonceStr: nonceStr, // 필수, 서명 생성용 랜덤 문자열
    //             signature: signature,// 필수, 서명 (부록1 참조)
    //             jsApiList: ['chooseImage', 'uploadImage', 'downloadImage', 'previewImage'] // 필수, 사용할 JS 인터페이스 목록 (전체 목록은 부록2 참조)
    //         });
    //
    //         wx.ready(function(){
    //
    //         });
    //     }
    // }
    // xmlhttp.open("GET","/getJSTicket",true);
    // xmlhttp.send();

    // var userToken = localStorage.getItem("userToken");
    // if ( userToken != null && userToken != "" && userToken != undefined) {
    //     alert("1인당 한 번만 체험할 수 있어요～");
    //     window.location.href = "result.html";
    // }


    wx.chooseImage({
        count: 1, // 기본값 9
        sizeType: ['compressed'], // 원본/압축 이미지 지정 가능, 기본은 둘 다 'original',
        sourceType: ['album','camera'], // 출처를 앨범/카메라로 지정 가능, 기본은 둘 다
        success: function (res) {
            var localIds = res.localIds.toString(); // 선택한 사진의 로컬 ID 목록을 반환한다. localId는 img 태그의 src 속성으로 이미지 표시에 쓸 수 있다

            wx.uploadImage({
                localId: localIds, // 업로드할 이미지의 로컬 ID. chooseImage 인터페이스로 얻는다
                isShowProgressTips: 1, // 기본값 1, 진행률 표시
                success: function (res) {
                    var userToken = localStorage.getItem("userToken");
                    if ( userToken == null || userToken == "" || userToken == undefined) {
                        userToken = randomString(10);
                        localStorage.setItem("userToken",userToken);
                    }
                    var serverId = res.serverId; // 이미지의 서버 측 ID를 반환한다
                    var url = "/recognizeFace?picId="+serverId+"&userToken="+userToken;

                    var xmlhttp;
                    if (window.XMLHttpRequest)
                    {// code for IE7+, Firefox, Chrome, Opera, Safari
                        xmlhttp=new XMLHttpRequest();
                    }
                    else
                    {// code for IE6, IE5
                        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
                    }
                    xmlhttp.onreadystatechange=function()
                    {
                        if (xmlhttp.readyState==4 && xmlhttp.status==200)
                        {
                            var data = JSON.parse(xmlhttp.responseText);
                            //no가 반환된 경우
                            if(data.success==false){
                                alert(data.message);
                            }

                            //yes가 반환된 경우
                            else{
                                var facePic = document.getElementById("facePic");
                                var uploadPic = document.getElementById("uploadPic");
                                var uploadPicTxt = document.getElementById("uploadPicTxt");
                                facePic.style.display = "block";
                                facePic.style.backgroundImage = "url("+data.faceUrl+")";
                                facePic.style.backgroundRepeat = "no-repeat";
                                facePic.style.backgroundSize = "cover";
                                facePic.style.backgroundPosition = "center center";
                                facePic.style.width = "100%";
                                facePic.style.height = "100%";

                                uploadPic.style.display = "none";
                                uploadPicTxt.style.display = "none";

                                // facePic.src = data.faceUrl;

                                var analysisResult = document.getElementById("analysisResult");
                                analysisResult.style.display = "block";
                                var gender = document.getElementById("gender");
                                // var age = document.getElementById("age");
                                var expression = document.getElementById("expression");
                                var glass = document.getElementById("glass");

                                gender.innerHTML = data.gender;
                                // age.innerHTML = data.age-5;
                                expression.innerHTML = data.expression;
                                glass.innerHTML = data.glass;

                                localStorage.setItem("gender",data.gender);
                                localStorage.setItem("expression",data.expression);
                                localStorage.setItem("glass",data.glass);
                                localStorage.setItem("faceUrl",data.faceUrl);

                                var startBtn = document.getElementById("startBtn");
                                startBtn.style.display = "block";

                                var phoneBtn = document.getElementById("phoneBtn");
                                phoneBtn.style.display = "none";

                                localStorage.setItem("resultUrl",data.resultUrl);

                                var faceBox = document.getElementById("faceBox");
                                faceBox.removeAttribute("onclick");
                            }
                        }
                    }
                    xmlhttp.open("GET",url,true);
                    xmlhttp.send();

                },
                fail: function (res) {
                    alert(JSON.stringify(res));
                }
            });
        }
    });


}

function upload2(){
    var facePic = document.getElementById("facePic");
    var uploadPic = document.getElementById("uploadPic");
    var uploadPicTxt = document.getElementById("uploadPicTxt");
    facePic.style.display = "block";
    facePic.style.backgroundImage = "url(http://www.chaimm.com/upload/ai/1515649931)";
    facePic.style.backgroundRepeat = "no-repeat";
    facePic.style.backgroundSize = "cover";
    facePic.style.backgroundPosition = "center center";
    facePic.style.width = "100%";
    facePic.style.height = "100%";

    uploadPic.style.display = "none";
    uploadPicTxt.style.display = "none";

    // facePic.src = data.faceUrl;

    var analysisResult = document.getElementById("analysisResult");
    analysisResult.style.display = "block";
    var gender = document.getElementById("gender");
    // var age = document.getElementById("age");
    var expression = document.getElementById("expression");
    var glass = document.getElementById("glass");

    gender.innerHTML = "남성";
    // age.innerHTML = "21";
    expression.innerHTML = "미소";
    glass.innerHTML = "안경 착용";

    var startBtn = document.getElementById("startBtn");
    startBtn.style.display = "block";

    var phoneBtn = document.getElementById("phoneBtn");
    phoneBtn.style.display = "none";

    // localStorage.setItem("resultUrl",data.resultUrl);

    var faceBox = document.getElementById("faceBox");
    faceBox.removeAttribute("onclick");
}


function init() {
    var faceUrl = localStorage.getItem("faceUrl");
    if ( faceUrl == null || faceUrl == "" || faceUrl == undefined) {
        config();
        return;
    }

    var facePic = document.getElementById("facePic");
    var uploadPic = document.getElementById("uploadPic");
    var uploadPicTxt = document.getElementById("uploadPicTxt");
    facePic.style.display = "block";
    facePic.style.backgroundImage = "url("+localStorage.getItem('faceUrl')+")";
    facePic.style.backgroundRepeat = "no-repeat";
    facePic.style.backgroundSize = "cover";
    facePic.style.backgroundPosition = "center center";
    facePic.style.width = "100%";
    facePic.style.height = "100%";

    uploadPic.style.display = "none";
    uploadPicTxt.style.display = "none";

    var analysisResult = document.getElementById("analysisResult");
    analysisResult.style.display = "block";
    var gender = document.getElementById("gender");
    // var age = document.getElementById("age");
    var expression = document.getElementById("expression");
    var glass = document.getElementById("glass");

    gender.innerHTML = localStorage.getItem("gender");
    // age.innerHTML = data.age-5;
    expression.innerHTML = localStorage.getItem("expression");
    glass.innerHTML = localStorage.getItem("glass");



    var resultBtn = document.getElementById("resultBtn");
    resultBtn.style.display = "block";

    var phoneBtn = document.getElementById("phoneBtn");
    phoneBtn.style.display = "none";

    var startBtn = document.getElementById("startBtn");
    phoneBtn.style.display = "none";

    var faceBox = document.getElementById("faceBox");
    faceBox.removeAttribute("onclick");
}

init();


function config() {
    // $.get("/getJSTicket",
    //     function (data, status) {
    //
    //         var nonceStr = randomString(16);
    //         var timestamp = Math.round(new Date().getTime() / 1000);
    //         var url = finishURL;
    //         var string1 = "jsapi_ticket=" + data + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
    //         var signature = hex_sha1(string1);
    //
    //         wx.config({
    //             debug: false, // 디버그 모드 활성화. 호출한 모든 api의 반환값이 클라이언트에 alert로 표시된다. 전달 파라미터를 보려면 PC에서 열면 되며, 파라미터 정보는 log로 출력되고 PC에서만 출력된다.
    //             appId: appID, // 필수, 공식 계정의 고유 식별자
    //             timestamp: timestamp, // 필수, 서명 생성 타임스탬프
    //             nonceStr: nonceStr, // 필수, 서명 생성용 랜덤 문자열
    //             signature: signature,// 필수, 서명 (부록1 참조)
    //             jsApiList: ['chooseImage', 'uploadImage', 'downloadImage', 'previewImage'] // 필수, 사용할 JS 인터페이스 목록 (전체 목록은 부록2 참조)
    //         });
    //     });

    var xmlhttp;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    }
    else
    {// code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)
        {
            var data = xmlhttp.responseText;

            var nonceStr = randomString(16);
            var timestamp = Math.round(new Date().getTime() / 1000);
            var url = finishURL;
            var string1 = "jsapi_ticket=" + data + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
            var signature = hex_sha1(string1);

            wx.config({
                debug: false, // 디버그 모드 활성화. 호출한 모든 api의 반환값이 클라이언트에 alert로 표시된다. 전달 파라미터를 보려면 PC에서 열면 되며, 파라미터 정보는 log로 출력되고 PC에서만 출력된다.
                appId: appID, // 필수, 공식 계정의 고유 식별자
                timestamp: timestamp, // 필수, 서명 생성 타임스탬프
                nonceStr: nonceStr, // 필수, 서명 생성용 랜덤 문자열
                signature: signature,// 필수, 서명 (부록1 참조)
                jsApiList: ['chooseImage', 'uploadImage', 'downloadImage', 'previewImage'] // 필수, 사용할 JS 인터페이스 목록 (전체 목록은 부록2 참조)
            });
        }
    }
    xmlhttp.open("GET","/getJSTicket",true);
    xmlhttp.send();
}

// config();


