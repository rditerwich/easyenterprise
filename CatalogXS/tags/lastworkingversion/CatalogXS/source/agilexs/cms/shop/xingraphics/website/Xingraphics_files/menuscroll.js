/*****************************************************
 * GlideMenus by Garrett Smith
 * 02-22-2004 v2.1
 * Licensed to:  Verisign
*****************************************************/
<!--
GlideParams = {
	START_SPEED : 400, // px per second
	HIDE_DELAY_MS : 300,
	SHOW_DELAY_MS : 200, // pause before showing submenu
	ACCELERATION_OUT :  .00055, // px/second/millisecond
	ACCELERATION_BACK : .0009, // px/second/millisecond
	FRAME_PAUSE : 1,
	CLICK_HIDE : false,
	CLICK_SHOW : false,
	IMG_OVER_EXT : "a"
};
//-->

GM_CREATOR="DHTMLKITCHEN";
GM_LICENSEE="Verisign";

ua=new function(){var u=navigator.userAgent,d=document;
this.ie=typeof d.all!="undefined";
this.ns4=typeof d.layers!="undefined";
this.dom=typeof d.getElementById!="undefined";
this.safari=/Safari/.test(u);
this.moz=/Gecko/.test(u)&&!this.safari;
this.mie=this.ie&&/Mac/.test(u);
this.win9x=/Win9/.test(u)||/Windows 9/.test(u);
this.o7=/Opera 7/.test(u);
this.supported=(typeof d.write!="undefined")&&(this.ie||this.ns4||this.dom);
this.px=this.dom&&d.compatMode!="BackCompat"||this.mie?"px":0;
this.dblThread=this.ie&&this.win9x||this.safari;};
if(GlideParams.CLICK_HIDE)document.onmousedown=function(e){var am=GlideMenus.activeMenu;
if(!am||contains(am.actuator,getTarget(e))||contains(am.el,getTarget(e)))return;
am.hideTree();
GlideMenus.activeMenu=null;};
function showMenu(e,id,dir,adjX,adjY){var menu=GlideMenu.getInstance(e,id,dir);
if(!menu.actuator)menu.actuator=menu.getActuator(e);
menu.over=true;
if(menu.parentMenu==null){var am=GlideMenus.activeMenu;
if(am&&am!=menu&&!am.closing()){clearTimeout(am.animTimer);
clearTimeout(am.animTimer2);
am.hideTree();}}window.clearTimeout(menu.hideTimer);
if(menu.closing())menu.glideBackEnd();
else clearTimeout(menu.showTimer);
if(GlideMenus.activeMenu&&GlideMenus.activeMenu==menu)return;
GlideMenus.activeMenu=menu;
clearTimeout(menu.showTimer);GlideMenu.positionFrame(menu,e,adjX,adjY);
menu.glideOutStart(true);};
function showSubMenu(e,parentMenuId,id,dir,adjX,adjY){var menu=GlideMenu.getInstance(e,id,dir);
if(!menu.actuator)menu.actuator=menu.getActuator(e);
clearTimeout(menu.hideTimer);
menu.over=true;
menu.parentMenu=GlideMenus[parentMenuId];
if(ua.ns4)adjX-=e.layerX;
GlideMenu.positionFrame(menu,e,adjX+menu.parentMenu.w(),adjY);
var activeMenu=menu.parentMenu.activeMenu;
if(activeMenu!=null&&activeMenu!=menu){if(!activeMenu.opening||!activeMenu.fullyOpen()){menu.showTimer=setTimeout(menu.GlideOutStartStr,GlideParams.SHOW_DELAY_MS);}else menu.parentMenu.activeMenu.menuInCue=menu;
return;}clearTimeout(menu.parentMenu.hideTimer);
if(menu.closing())menu.glideBackEnd();
else if(menu.fullyClosed())menu.showTimer=setTimeout(menu.GlideOutStartStr,GlideParams.SHOW_DELAY_MS);}
function hideSubMenu(id){var menu=GlideMenus[id];
clearTimeout(menu.showTimer);
if(menu.parentMenu!=null&&menu.parentMenu.activeMenu!=null&&menu.parentMenu.activeMenu.menuInCue!=null&&menu.parentMenu.activeMenu.menuInCue!=menu){menu.parentMenu.activeMenu.menuInCue.hide();}window.clearTimeout(menu.hideTimer);
menu.over=false;
if(menu.parentMenu&&(!menu.parentMenu.over||GlideParams.CLICK_HIDE))return;
menu.hideTimer=window.setTimeout(menu.hideStr,GlideParams.HIDE_DELAY_MS);}function headerMouseover(imgName_or_element){if(GlideMenus.activeMenu)GlideMenus.activeMenu.hideTree();
if(typeof imgName_or_element=="string"){var img=document.images[imgName_or_element];
img.lowSrc=img.src;
img.src=img.src.replace(Exps.EXT,GlideParams.IMG_OVER_EXT+"$1");}else{var el=imgName_or_element;
el.className+=" hover";}}function headerMouseout(imgName_or_element){if(typeof imgName_or_element=="string"){var img=document.images[imgName_or_element];
img.src=img.lowSrc;}else{var el=imgName_or_element;
removeClass(el,"hover");}}function actuatorMouseover(event,menuId,dir,adjX,adjY){if(adjX==null)adjX=0;
if(adjY==null)adjY=0;
if(GlideParams.CLICK_SHOW){var menu=GlideMenu.getInstance(event,menuId,dir);
menu.getActuator(event);
menu.hiliteHdr();
if(GlideMenus.activeMenu)if(GlideMenus.activeMenu.id!=menuId){hideMenu(GlideMenus.activeMenu.id);
showMenu(event,menuId,dir,adjX,adjY);}else
GlideMenus.activeMenu.glideOutStart();}else showMenu(event,menuId,dir,adjX,adjY);}function actuatorMouseout(event,menuId){if(GlideParams.CLICK_SHOW){var menu=GlideMenu.getInstance(event,menuId,"");
menu.getActuator(event);
menu.over=false;
if(menu.fullyClosed())menu.unhiliteHdr();}else hideMenu(menuId);}
function hideMenu(id){var menu=GlideMenus[id];
if(menu==null||menu.frame==null)return;
if(!menu.opening&&!menu.fullyOpen())return;
window.clearTimeout(menu.hideTimer);
window.clearTimeout(menu.showTimer);
if(!menu.opening){window.clearInterval(menu.animTimer);
window.clearInterval(menu.animTimer2);}if(menu.activeMenu)menu.activeMenu.hideTimer=window.setTimeout(menu.activeMenu.hideStr,1100);
menu.over=false;
if(menu.activeMenu)return;
if(GlideParams.CLICK_HIDE)return;
menu.hideTimer=window.setTimeout(menu.hideStr,GlideParams.HIDE_DELAY_MS);
clearTimeout(menu.showTimer);}GlideMenu=function(e,id,dir){if(!ua.supported)return;
var d=window.document;
this.actuator=(e==null)?null:this.getActuator(e);
this.id=id;
this.dir=dir;
var fID=id+"Frame";
this.frame=ua.dom?d.getElementById(fID):ua.ie?d.all[fID]:d.layers[fID];
if(ua.ns4&&!this.frame&&d.parentLayer)this.frame=d.parentLayer.d.layers[fID];
if(ua.ns4)this.frame.style=this.frame;
this.el=ua.dom?d.getElementById(id):ua.ns4?this.frame.layers[id]:d.all[id];
this.css=ua.ns4?this.el:this.el.style;
if(!ua.ns4&&!ua.mie){if(!ua.o7)this.css.height=this.css.width="auto";
this.frame.style.width=this.el.offsetWidth+"px";}if(this.dir.indexOf("e")>=0)this.startX=-this.w();
else if(this.dir.indexOf("w")>=0)this.startX=this.w();
if(this.dir.indexOf("n")>=0)this.startY=this.h();
else if(this.dir.indexOf("s")>=0)this.startY=-this.h();
var cId=id;
if(ua.ns4){this.frame.captureEvents(Event.MOUSEMOVE);
this.frame.onmousemove=function(e){GlideMenu.onmouseover(cId);var sub;
if((sub=GlideMenus[cId].activeMenu)==null)return;
if(e.y>sub.actuator.y+14||e.y<sub.actuator.y-1||e.pageX<this.x||e.pageX>this.x+this.clip.width){sub.over=false;
sub.hideTimer=setTimeout(sub.hideStr,GlideParams.HIDE_DELAY_MS);}};}else{this.el.onmouseover=function(){GlideMenu.onmouseover(e,cId);};	this.el.onmouseout=function(e){GlideMenu.onmouseout(e,cId);};}var ref="GlideMenus."+this.id;
this.GlideOutStartStr=ref+".glideOutStart(true)";
this.GlideOutStr=ref+".glideOut()";
this.GlideBackStr=ref+".glideBack()";
this.hideStr=ref+".hide()";
this.moveTo(this.startX,this.startY);
this.initAnimProps();GlideMenus[id]=this;};
GlideMenus={activeMenu:null};
GlideMenu.getInstance=function(e,id,dir){if(GlideMenus[id]==null)GlideMenus[id]=new GlideMenu(e,id,dir);
return GlideMenus[id];};
GlideMenu.onmouseover=function(e,cId){var menu=GlideMenus[cId];
var toEl=e?(window.event)?window.event.toElement:e.relatedTarget:null;
menu.over=true;
if(GlideParams.CLICK_HIDE){if(menu.activeMenu&&!menu.activeMenu.over){if(contains(menu.el,toEl))menu.activeMenu.hide();
else
if(!menu.activeMenu.hideTimer)hideSubMenu(menu.activeMenu.id);}return;}clearTimeout(menu.hideTimer);
if(!menu.opening&&!menu.fullyOpen()){clearInterval(menu.animTimer);
clearInterval(menu.animTimer2);
menu.glideOutStart(false);}if(menu.parentMenu){menu.parentMenu.over=false;
clearTimeout(menu.parentMenu.hideTimer);}};
GlideMenu.onmouseout=function(e,cId){var toEl=(window.event)?window.event.toElement:e.relatedTarget;
if(contains(GlideMenus[cId].el,toEl))return;
hideMenu(cId);};
GlideMenu.positionFrame=function(menu,e,adjX,adjY){if(ua.ns4)return;
if(typeof adjX=="number"){if(ua.mie&&menu.actuator){var actuatorPaddingL=parseInt(menu.actuator.currentStyle.paddingLeft);
if(!isNaN(actuatorPaddingL))adjX-=actuatorPaddingL;}var left=getPageOffset(menu.actuator,"offsetLeft");
menu.frame.style.left=Math.round(left+adjX)+ua.px;}if(typeof adjY=="number"){var top=(ua.ns4?e.y-menu.actuator.y:getPageOffset(menu.actuator,"offsetTop"));
var elHeight=menu.actuator.offsetHeight;
menu.frame.style.top=Math.round(top+adjY+elHeight)+ua.px;}};
GlideMenu.prototype={el:null,css:null,frame:null,actuator:null,imgSrc:null,hiSrc:null,parentMenu:null,activeMenu:null,showTimer:0,hideTimer:0,animTimer:0,animTimer2:0,startX:0,endX:0,startY:0,endY:0,dir:"s",GlideOutStartStr:"",GlideOutStr:"",GlideBackStr:"",hideStr:"",opening:false,over:false,GlideDist:0,rx:0,ry:0,del:0,glideOutStart:function(bActivate){if(bActivate)this.onactivate();
if(this.fullyOpen())return;
this.hiliteHdr();
if(this.opening)clearTimeout(this.hideTimer);
this.opening=true;
if(this.parentMenu)this.parentMenu.activeMenu=this;
this.GlideStartOffset=this.GlideDist-Math.sqrt(this.x*this.x+this.y*this.y);
this.frame.style.visibility="visible";
if(this.parentMenu==null)GlideMenus.activeMenu=this;
window.clearInterval(this.animTimer);
window.clearInterval(this.animTimer2);
this.startTime=new Date().getTime();
this.animTimer=window.setInterval(this.GlideOutStr,this.del);
if(ua.dblThread)this.animTimer2=window.setInterval(this.GlideOutStr,this.del);},glideBackStart:function(){this.ondeactivate();
this.unhiliteHdr();
if(this.fullyClosed())return;
this.opening=false;if(this.parentMenu&&this.parentMenu.activeMenu==this)this.parentMenu.activeMenu=null;
this.GlideBackOffset=-(this.rx*this.x+this.ry*this.y);
window.clearInterval(this.animTimer);
window.clearInterval(this.animTimer2);
this.startTime=new Date().getTime();
this.animTimer=window.setInterval(this.GlideBackStr,this.del);
if(ua.dblThread)this.animTimer2=window.setInterval(this.GlideBackStr,this.del);},glideOut:function(){var t=new Date().getTime()-this.startTime;
var d=Math.ceil(GlideParams.START_SPEED/1000*t+.5*GlideParams.ACCELERATION_OUT*t*t)+this.GlideStartOffset;
if(d>=this.GlideDist){this.moveTo(0,0);
this.glideOutEnd();}else{this.moveTo(this.startX+d*this.rx,this.startY+d*this.ry);}},glideBack:function(){var t=new Date().getTime()-this.startTime;
var d=Math.ceil(GlideParams.START_SPEED/1000*t+.5*GlideParams.ACCELERATION_BACK*t*t)+this.GlideBackOffset;
if(d>=this.GlideDist){this.moveTo(this.startX,this.startY);
this.glideBackEnd();}else{this.moveTo(-d*this.rx,-d*this.ry);}},glideOutEnd:function(){this.animTimer=window.clearTimeout(this.animTimer);
this.animTimer2=window.clearTimeout(this.animTimer2);
if(!this.parentMenu)GlideMenus.activeMenu=this;
else if(ua.ns4);},glideBackEnd:function(){this.animTimer=window.clearTimeout(this.animTimer);
this.animTimer2=window.clearTimeout(this.animTimer2);
if(this.menuInCue&&this.menuInCue.over){if(this.parentMenu)(this.parentMenu.activeMenu=this.menuInCue).glideOutStart();
this.menuInCue=null;}else if(!this.parentMenu&&!this.menuInCue&&GlideMenus.activeMenu==this&&!GlideParams.CLICK_HIDE)GlideMenus.activeMenu=null;
if(this.parentMenu){if(!this.parentMenu.over&&this.fullyClosed()&&!GlideParams.CLICK_HIDE)this.parentMenu.glideBackStart();
else if(this.over)this.glideOutStart(false);
if(!GlideParams.CLICK_HIDE)this.parentMenu=null;}if(!this.over)this.frame.style.visibility="hidden";},hiliteHdr:function(){if(!this.actuator)return;
var src;
if((src=this.actuator.src)!=null){if(!this.imgSrc){this.imgSrc=src;
this.hiSrc=new Image().src=src.replace(Exps.EXT,GlideParams.IMG_OVER_EXT+"$1");}this.actuator.src=this.hiSrc;}if(!ua.ns4){this.actuator.className=(this.actuator.className+" hover").trim();
this.frame.style.zIndex=(this.parentMenu)?parseInt(this.parentMenu.frame.style.zIndex)+1:100;}},unhiliteHdr:function(){if(!this.actuator)return;
if(this.actuator.src){this.actuator.src=this.imgSrc;}if(!ua.ns4){removeClass(this.actuator,"hover");if(!this.parentMenu)this.frame.style.zIndex="";}},hide:function(){if(this.over)return;
this.activeMenu=null;
window.clearTimeout(this.hideTimer);
if(this.opening||this.fullyOpen()){this.unhiliteHdr();
this.glideBackStart();}else if(!this.closing())this.parentMenu=null;},hideTree:function(){var am=this;
for(;am.activeMenu!=null;am=am.activeMenu);
for(var p=am;(p=p.parentMenu)!=null;p.hide());
am.hide();},moveTo:(ua.ie?function(x,y){this.x=this.css.posLeft=x;this.y=this.css.posTop=y;}:function(x,y){this.css.left=(this.x=x)+ua.px;
this.css.top=(this.y=y)+ua.px;}),closing:function(){return this.animTimer&&!this.opening;},fullyOpen:function(){return this.x==0&&this.y==0;},fullyClosed:function(){return this.x==this.startX&&this.y==this.startY;},overSubmenu:function(){for(var menu=this;menu!=null;menu=menu.activeMenu)if(menu.over)return true;
return false;},initAnimProps:function(){this.GlideDist=Math.ceil(Math.sqrt(Math.pow(this.startX,2)+Math.pow(this.startY,2)));
this.rx=-this.startX/this.GlideDist;
this.ry=-this.startY/this.GlideDist;
this.del=GlideParams.FRAME_PAUSE;
if(ua.win9x&&ua.ie&&this.del<54)this.del=Math.floor(Math.pow(this.del,1-(.54-this.del/100)));},
w:function(){return ua.ns4?this.frame.clip.width:this.el.offsetWidth;},h:function(){return ua.ns4?this.frame.clip.height:this.el.offsetHeight;},getActuator:function(e){var el=getTarget(e);
return el?(el.nodeType==3)?el.parentNode:el:null;},toString:function(){return this.id;},onactivate:function(){},ondeactivate:function(){}};
if(ua.supported)document.writeln("<style type='text/css'>.menuFrame{visibility:hidden;}<"+"/style>");
function contains(a,b){if(b==null)return false;
while(a!=b&&((b=b.parentNode)!=null||ua.ns4&&(b=b.parentLayer)!=null));
return a==b;}function getPageOffset(el,off){var total=0;
if(typeof el[off]=="number")for(var parent=el;parent.offsetParent!=null;parent=parent.offsetParent)total+=parent[off];
return total;}function getTarget(e){return e&&e.target?e.target:(window.event)?event.srcElement:null;}function removeClass(el,klass){el.className=el.className.replace(getTokenizedExp(klass,"g")," ").normalize();}String.prototype.normalize=function(){return this.trim().replace(Exps.NORMALIZE," ");};
String.prototype.trim=function(){return this.replace(Exps.TRIM,"");};
Exps={NORMALIZE:/\s\s+/g,TRIM:/^\s+|\s+$/g,EXT:/(\.(.[^\.]+)$)/};
function getTokenizedExp(t,f){var x=Exps[t];if(!x)x=Exps[t]=new RegExp("(^|\\s)"+t+"($|\\s)",f);return x;}function getCookie(name){var match=(new RegExp(name+'\s?=\s?([^;]*);?','g')).exec(document.cookie)||[];
return match.length>1?unescape(match[1]):null;}