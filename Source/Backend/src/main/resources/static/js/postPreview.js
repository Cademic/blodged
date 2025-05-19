$(document).ready(() => {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("scrollTo")) {
        $("#" + urlParams.get("scrollTo"))[0].scrollIntoView({
            behavior: "instant",
            block: "start"
        })
    }
})
function likePost(pid) {
    const isLiked = $(`#post-isLiked-${pid}`).val()
    if (isLiked === "true") {
        $("#post-isLiked-" + pid).val(false)
        fetch(`/unlike/${pid}`, {
            method: "POST"
        }).then(async resp => {
            if (resp.status === 200) {
                $("#like-icon-" + pid).removeClass("liked")
                const lc = $("#like-count-" + pid)
                lc.text(parseInt(lc.text()) - 1)
                //window.location.reload()
            } else {
                $("#post-isLiked-" + pid).val(true)
                const text = await resp.text()
                alert("Couldn't unlike post! '" + text + "'")
            }
        })
    } else {
        $("#post-isLiked-" + pid).val(true)
        // like
        fetch(`/like/${pid}`, {
            method: "POST"
        }).then(async resp => {
            if (resp.status === 200) {
                $("#like-icon-" + pid).addClass("liked")
                const lc = $("#like-count-" + pid)
                lc.text(parseInt(lc.text()) + 1)
                //window.location.reload()
            } else {
                $("#post-isLiked-" + pid).val(false)
                const text = await resp.text()
                alert("Couldn't like post! '" + text + "'")
            }
        })
    }
}
function editPost(pid) {
        fetch('/posts/edit/' + pid, {
            method: "PUT",
            body: JSON.stringify({
                content: $("#post-edit-box-" + pid ).val()
            }),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        }).then(async resp => {
            if (resp.status == 200) {
                window.location = window.location.protocol + "//" + window.location.host + window.location.pathname + "?scrollTo=post-content-" + pid
            } else {
                const text = await resp.text()
                alert("Can't edit post! '" + text +"'")
            }
        })

}
function deletePost(pid) {
    if (confirm("Are you sure you want to delete this post?")) {
        fetch('/posts/delete/' + pid, {
            method: "DELETE",
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        }).then(async resp => {
            if (resp.status == 200) {
                window.location.reload()
            } else {
                const text = await resp.text()
                alert("Couldn't delete post! '" + text + "'")
            }
        })
    }
}
function cancelEdit(pid) {
    window.location = window.location.protocol + "//" + window.location.host + window.location.pathname + "?scrollTo=post-content-" + pid
}
function openEdit(pid) {
    window.location = window.location.protocol + "//" + window.location.host + window.location.pathname + "?toEdit=" + pid + "&scrollTo=post-edit-box-" + pid
}