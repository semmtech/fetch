(this.webpackJsonpapp=this.webpackJsonpapp||[]).push([[0],{255:function(e,t,n){"use strict";n.r(t);var a=n(0),r=n.n(a),c=n(15),o=n.n(c),l=n(26),i=n(36),u=n(34),s=n(27),d=n(87),p=n.n(d),f=n(28),m=n(90),h=n.n(m),g=n(35),E=n.n(g),b=n(51),w=n.n(b),v=n(88),y=n.n(v),x=new URLSearchParams(window.location.search),O=function(e,t){return x.has(e)?r.a.createElement("span",{"data-testid":"feedbackMessage"},t):void 0},j={cursor:"pointer",color:"#4C4C4C",minWidth:"50px",textAlign:"center"},k="Username",C="Password",L="Show password",P="Hide password";function S(){var e=Object(l.a)(["\n  color: #4c4c4c;\n"]);return S=function(){return e},e}function z(){var e=Object(l.a)(["\n  width: 100%;\n  margin: 10px;\n  font-size: 20px;\n  display: flex;\n"]);return z=function(){return e},e}function A(){var e=Object(l.a)(["\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n"]);return A=function(){return e},e}function W(){var e=Object(l.a)(["\n  padding: 20px;\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n"]);return W=function(){return e},e}var F=function(){var e=Object(a.useState)(!0),t=Object(i.a)(e,2),n=t[0],c=t[1],o=Object(a.useState)(""),l=Object(i.a)(o,2),s=l[0],d=l[1],m=Object(a.useState)(""),g=Object(i.a)(m,2),b=g[0],v=g[1],x=Object(u.useGlobal)("login"),S=function(){c(!n)};return r.a.createElement(I,null,r.a.createElement("img",{src:y.a,alt:"Laces logo",height:"250",width:"250"}),r.a.createElement(R,null,"Laces Fetch"),r.a.createElement(B,null,r.a.createElement(J,null,r.a.createElement(w.a,{id:"userName",fullWidth:!0,autoFocus:!0,value:b,onChange:function(e){return v(e.target.value)},type:"text",placeholder:k,InputProps:{startAdornment:r.a.createElement(E.a,{position:"start"},r.a.createElement(f.d,{size:25,style:{color:"#4c4c4c"}}))}})),r.a.createElement(J,null,r.a.createElement(w.a,{id:"password",fullWidth:!0,type:n?"password":"text",placeholder:C,onChange:function(e){return d(e.target.value)},onKeyPress:function(e){"Enter"===e.key&&x(b,s)},value:s,InputProps:{endAdornment:r.a.createElement(E.a,{position:"end"},n?r.a.createElement(f.a,{"data-tip":L,onClick:S,size:25,style:j}):r.a.createElement(f.b,{"data-tip":P,onClick:S,size:25,style:j})),startAdornment:r.a.createElement(E.a,{position:"start"},r.a.createElement(f.c,{size:25,style:{color:"#4c4c4c"}}))}}),r.a.createElement(p.a,{place:"right",type:"dark",effect:"float"})),r.a.createElement(J,null,r.a.createElement(h.a,{"data-testid":"LoginButton",fullWidth:!0,style:{backgroundColor:"#F6E524",color:"#4c4c4c"},onClick:function(){return x(b,s)}},"Login"))),O("logout","You have been logged out, please login again if you want to use the application."),O("error","Login was unsuccessfull, please try again."))},I=s.a.div(W()),B=s.a.form(A()),J=s.a.div(z()),R=s.a.h1(S()),U=n(54),G=n.n(U),H=n(93);Object(u.addReducer)("login",function(){var e=Object(H.a)(G.a.mark((function e(t,n,a){return G.a.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",fetch("/authenticate?username=".concat(n,"&password=").concat(a),{credentials:"include",method:"POST"}).then((function(e){return window.location.href=e.url})).catch((function(e){return{error:e}})));case 1:case"end":return e.stop()}}),e)})));return function(t,n,a){return e.apply(this,arguments)}}()),o.a.render(r.a.createElement((function(){return r.a.createElement(F,null)}),null),document.getElementById("root"))},88:function(e,t,n){e.exports=n.p+"static/media/Laces.fcddcf17.png"},96:function(e,t,n){e.exports=n(255)}},[[96,1,2]]]);
//# sourceMappingURL=main.190389cc.chunk.js.map