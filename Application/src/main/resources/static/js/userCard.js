function unfollow(uid) {
    fetch("/accounts/unfollow", {
        method: "POST",
        body: JSON.stringify({
            userToFollow: uid
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).then(async resp => {
        if (resp.status == 200) {
            window.location.reload()
        } else {
            const text = await resp.text()
            alert("Could not unfollow user: '" + text + "'")
        }
    })
}
function mouseEnter(uid) {
    const el = $(`#unfollow-${uid}`)
    if (el.text() != "Unfollow?") el.text("Unfollow?")
}
function mouseLeave(uid) {
    const el = $(`#unfollow-${uid}`)
    if (el.text() != "Following") el.text("Following")
}