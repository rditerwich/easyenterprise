// 元素是否有垂直滚动条
function hasVerticalScrollbar(e) {
	return e.scrollHeight > e.clientHeight
}

// 元素是否有水平滚动条
function hasHorizontalScrollbar(e) {
	return e.scrollWidth > e.clientWidth
}

// 取得浏览器滚动条的宽度, in safari = 0
function getScrollbarWidth() {
	var div = $('<div style="width:50px;height:50px;overflow:hidden;position:absolute;top:-200px;left:-200px;"><div style="height:100px;"></div></div>');
	// Append our div, do our calculation and then remove it
	$('body').append(div);
	var w1 = $('div', div).innerWidth();
	div.css('overflow-y', 'scroll');
	var w2 = $('div', div).innerWidth();
	$(div).remove();
	return (w1 - w2);
}

function GetEvent()
{
	if (typeof window.event == "undefined")
	{
		var func = arguments.callee.caller;
		while(func != null)
		{
			var arg0 = func.arguments[0];
			if(arg0 && (arg0.constructor == Event || arg0.constructor == MouseEvent || arg0.constructor == KeyboardEvent))
				return arg0;
			func = func.caller;
		}
		return null;
	}
	else
		return window.event;
}


function GetQueryString(url) {
	if (!url) var url = window.location.href;
	var query = url.replace(/^[^\?]+\??/, '');
	var pairs = query.split(/[;&]/);
	var params = {};

	for (var i = 0; i < pairs.length; i++) {
		var keyVal = pairs[i].split('=');
		if (!keyVal || keyVal.length != 2 ) continue;
		var key = decodeURIComponent(keyVal[0]);
		var val = decodeURIComponent(keyVal[1]);
		val = val.replace(/\+/g, ' ');
		params[key] = val;
	}

	return params;
}


function ChangeUrl(keys, vals, url, returnValue) {
	if (!url) url = window.location.href;
	var params = GetQueryString(url);

	if (keys.join) {
		for (var i = 0; i < keys.length; i++)
			params[keys[i]] = vals.join ? vals[i] : i;
	} else {
		params[keys] = vals;
	}

	var keyVal = [];
	for (var key in params) {
		if (null != params[key]) keyVal.push(key + '=' + params[key]);
	}

	url += '?';
	var query = keyVal.join('&');
	var position = (url + '?').indexOf('?') + 1;
	url = url.substr(0, position) + query;
	if (returnValue)
		return url
	else
		location.href = url;
}

function InsertInterferenceCode() {
	document.body.oncopy = function () {
		setTimeout( function () {
			var text = clipboardData.getData("text");
			if (text) {
				text = text + "\r\n干扰码";
				clipboardData.setData("text", text);
			}
		}, 100 );
	}
}

/*
 定义关联下拉列表框类
 parentId -> 包含所有下拉列表框的网页元素 id 值
 itemInAll -> 在所有下拉列表框中生成 itemInAll 选项, null 为不生成 (可选项)
 itemInFirst -> 在第一个下拉列表框中生成 itemInFirst 选项, null 为不生成, 其优先级大于 itemInAll (可选项)
*/
function RelatingList(parentId, itemInAll, itemInFirst)
{
	if (typeof parentId == "undefined") return false;
	if (typeof itemInAll == "undefined") itemInAll = null;
	if (typeof itemInFirst == "undefined") itemInFirst = null;

	this.parentId = parentId;
	this.itemInAll = itemInAll;
	this.itemInFirst = itemInFirst;
	this.item = Array();
}

/*
 添加一个列表项
 id -> 列表项的 id
 name -> 列表项的名称
 parent -> 列表项的父项 id
*/
RelatingList.prototype.AddItem = function(id, name, cat)
{
	with(this)
	{
		item[item.length] = {
			id : id,
			name : name,
			parentId : cat
		};
	}
}

/* 根据设置初始化关联下拉列表框 */
RelatingList.prototype.Show = function(position)
{
	position = position == "" ? [""] : position.split(",");
	with(this)
	{
		var eParent =  document.getElementById(parentId);

		// 生成隐藏提交表单(父类框名称: parentId_cat, 位置框名称: parentId_position)
		var eInput = document.createElement("INPUT");
		eInput.setAttribute("type", "hidden");
		eInput.setAttribute("id", parentId + "_cat");
		eInput.setAttribute("name", parentId + "_cat");
		eParent.parentNode.appendChild(eInput);
		var eInputPosition = document.createElement("INPUT");
		eInputPosition.setAttribute("type", "hidden");
		eInputPosition.setAttribute("id", parentId + "_position");
		eInputPosition.setAttribute("name", parentId + "_position");
		eParent.parentNode.appendChild(eInputPosition);

		this.GenerateChildList();
		for (var i = 0; i < position.length; i++)
		{
			var eSelect = eParent.childNodes[i];
			if (eSelect)
			{
				if (position[i] == "" || position[i] == "0")
					eSelect.selectedIndex = 0;
				else
					eSelect.value = position[i];
				if (eSelect.value == "") return;
				$(eSelect).change();
			}
		}
	}
}

/*
 生成子类列表框
 curList -> 当前下拉列表框元素 (可选项)
*/
RelatingList.prototype.GenerateChildList = function(curList)
{
	var parent = 0;	// 父下拉列表框的值
	if (arguments.length != 0) parent = parseInt(curList.value);

	with(this)
	{
		var eParent =  document.getElementById(parentId);

		// 删除当前下拉列表框后面的所有下拉列表框
		while (eParent.lastChild && eParent.lastChild != curList)
			eParent.removeChild(eParent.lastChild);
		// 创建一个下拉列表框
		var eSelect = document.createElement("SELECT");
		var o = this;
		eSelect.onchange = function() {o.GenerateChildList(this);};

		// 生成 itemInAll 和 itemInFirst 选项
		if (itemInFirst && eParent.childNodes.length == 0)
		{
			var eOption = document.createElement("OPTION");
			eOption.style.color = "red"
			eSelect.options.add(eOption);
			eOption.value = "";
			eOption.text = itemInFirst;
		}
		else if (itemInAll)
		{
			var eOption = document.createElement("OPTION");
			eOption.style.color = "red";
			eSelect.options.add(eOption);
			eOption.value = "";
			eOption.text = itemInAll;
		}

		// 生成下拉列表项
		for (var i = 0; i < item.length; i++)
		{
			if (item[i].parentId === parent)
			{
				var eOption = document.createElement("OPTION");
				eSelect.options.add(eOption);
				eOption.value = item[i].id;
				eOption.text = item[i].name;
			}
		}
		// 生成的下拉列表框是否有有效的列表项, 有则显示
		if (eSelect.options.length > 1 || eSelect.options[0] && eSelect.options[0].value != "")
		{
			eParent.appendChild(eSelect);
			$(eSelect).change();
		}

		// 更新提交的父类值
		var eInputParent = document.getElementById(parentId + "_cat");
		var eSelect = eParent.lastChild;
		if (eParent.childNodes.length > 1 && eSelect.value == "") eSelect = eSelect.previousSibling;
		eInputParent.value = (eSelect && eSelect.value != "" ? eSelect.value : 0);
		// 更新提交的位置值
		var eInputPosition = document.getElementById(parentId+ "_position");
		var pos = "";
		var eSelect = eParent.firstChild;
		while (eSelect && eSelect.value != "")
		{
			pos += "," + eSelect.value;
			eSelect = eSelect.nextSibling;
		}
		eInputPosition.value = pos.substr(1);
	}
}

top.adminKey = false;
$(document).keydown(function (e) {
	var key = e.charCode || e.keyCode || 0;
	try {if (key == '192') top.adminKey = true;} catch(e) {}
});
$(document).keyup(function (e) {
	var key = e.charCode || e.keyCode || 0;
	try {if (key == '192') top.adminKey = false;} catch(e) {}
});
$(document).dblclick(function () {
	if (top.adminKey === true) {
		var href = window.location.href + '/';
		href = href.substr(0, href.indexOf("/", 8)) + "/admin/";
		window.open(href);
	}
});

$(function() {
	var curCat = (((location.pathname.split('/')).pop()).split('.').shift()).split('-').shift() || 'index';
	var isEn = location.pathname.substr(0, 4).toLowerCase() == '/en/';
	if (curCat == 'service') curCat = 'service-1';
	var a = $('#mainmenu a[href=' + curCat + '.php]:first');
	if (a.length) {
		if (isEn && (curCat == 'news' || curCat == 'service-1'))
			var newClass = a.parent().attr('class') + 'a';
		else
			var newClass = a.parent().attr('class') + '1';
		a.parent().removeClass().addClass(newClass);

		var img = a.find('img');
		if (img.length) {
			var imgsrc = img.attr('src');
			pos = imgsrc.lastIndexOf('.');
			var newsrc = imgsrc.substr(0, pos) + 'a' + imgsrc.substr(pos);
			img.attr('src', newsrc);
			a.mouseout(function(){img.attr('src', newsrc);});
		}
	}
})