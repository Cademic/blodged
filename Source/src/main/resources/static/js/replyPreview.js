function editReply(pid) {
    fetch('/replies/edit/' + pid, {
        method: "PUT",
        body: JSON.stringify({
            content: $("#reply-edit-box-" + pid ).val()
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).then(async resp => {
        if (resp.status == 200) {
            window.location = window.location.protocol + "//" + window.location.host + window.location.pathname + "?scrollTo=reply-content-" + pid
        } else {
            const text = await resp.text()
            alert("Can't edit reply! '" + text +"'")
        }
    })

}
function deleteReply(pid) {
if (confirm("Are you sure you want to delete this reply?")) {
    fetch('/replies/delete/' + pid, {
        method: "DELETE",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).then(async resp => {
        if (resp.status == 200) {
            window.location.reload()
        } else {
            const text = await resp.text()
            alert("Couldn't delete reply! '" + text + "'")
        }
    })
}
}
function cancelEditReply(pid) {
    window.location = window.location.protocol + "//" + window.location.host + window.location.pathname + "?scrollTo=reply-content-" + pid
}
function openEditReply(pid) {
    window.location = window.location.protocol + "//" + window.location.host + window.location.pathname + "?toEditReply=" + pid + "&scrollTo=reply-edit-box-" + pid
}