var canvas = document.getElementById("cas");
var ctx = canvas.getContext("2d");
resize();
window.onresize = resize;
function resize() {
    canvas.width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    canvas.height = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
}
var RAF = (function() {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function(callback) {
        window.setTimeout(callback, 1000 / 120);
    };
})();
// 마우스가 움직일 때 마우스 좌표를 가져온다
var warea = {x: null, y: null, max: 20000};
window.onmousemove = function(e) {
    e = e || window.event;
    warea.x = e.clientX;
    warea.y = e.clientY;
};
window.onmouseout = function(e) {
    warea.x = null;
    warea.y = null;
};
// 입자 추가
// x, y는 입자 좌표, xa, ya는 입자의 x·y축 가속도, max는 선을 잇는 최대 거리
var dots = [];
for (var i = 0; i < 40; i++) {
    var x = Math.random() * canvas.width;
    var y = Math.random() * canvas.height;
    var xa = Math.random() * 2 - 1;
    var ya = Math.random() * 2 - 1;
    dots.push({
        x: x,
        y: y,
        xa: xa,
        ya: ya,
        max: 6000
    })
}
// 애니메이션을 지연 후 시작한다. 즉시 실행하면 간혹 위치 계산이 틀어질 수 있다
setTimeout(function() {
    animate();
}, 100);
// 매 프레임마다 반복되는 로직
function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    // 마우스 좌표를 추가해, 거리 비교에 쓸 점 배열을 만든다
    var ndots = [warea].concat(dots);
    dots.forEach(function(dot) {
        // 입자 이동
        dot.x += dot.xa;
        dot.y += dot.ya;
        // 경계에 닿으면 가속도를 반대로 뒤집는다
        dot.xa *= (dot.x > canvas.width || dot.x < 0) ? -1 : 1;
        dot.ya *= (dot.y > canvas.height || dot.y < 0) ? -1 : 1;
        // 점 그리기
        ctx.fillRect(dot.x - 0.5, dot.y - 0.5, 1, 1);
        // 입자 사이의 거리를 반복해서 비교한다
        for (var i = 0; i < ndots.length; i++) {
            var d2 = ndots[i];
            if (dot === d2 || d2.x === null || d2.y === null) continue;
            var xc = dot.x - d2.x;
            var yc = dot.y - d2.y;
            // 두 입자 사이의 거리
            var dis = xc * xc + yc * yc;
            // 거리 비율
            var ratio;
            // 두 입자 사이의 거리가 입자 객체의 max 값보다 작으면 두 입자 사이에 선을 긋는다
            if (dis < d2.max) {
                // 마우스라면 입자를 마우스 위치 쪽으로 이동시킨다
//                        if (d2 === warea && dis > (d2.max / 2)) {
//                            dot.x -= xc * 0.03;
//                            dot.y -= yc * 0.03;
//                        }
                // 거리 비율 계산
                ratio = (d2.max - dis) / d2.max;
                // 선 긋기
                ctx.beginPath();
                ctx.lineWidth = ratio / 2;
                ctx.strokeStyle = 'rgba(201,203,206,' + (ratio + 0.2) + ')';
                ctx.moveTo(dot.x, dot.y);
                ctx.lineTo(d2.x, d2.y);
                ctx.stroke();
            }
        }
        // 이미 계산한 입자를 배열에서 제거한다
        ndots.splice(ndots.indexOf(dot), 1);
    });
    RAF(animate);
}
