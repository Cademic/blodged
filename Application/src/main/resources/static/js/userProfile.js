let fbh = false
function followBtnHover() {
    const followBtn = $("#followBtn")
    fbh = !fbh
    if ($("#following").val() == "true") {
        if (fbh) {
            followBtn.text("Unfollow?")
        } else {
            followBtn.text("Following")
        }
    }
}
function followUser() {
    const profileUserId = $("#profileUserId").val()
    fetch("/accounts/follow", {
        method: "POST",
        body: JSON.stringify({
            userToFollow: profileUserId
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).then(async resp => {
        if (resp.status == 200) {
            window.location.reload()
        } else {
            const text = await resp.text()
            alert("Could not follow user: '" + text + "'")
        }
    })
}
function unfollowUser() {
    const profileUserId = $("#profileUserId").val()
    fetch("/accounts/unfollow", {
        method: "POST",
        body: JSON.stringify({
            userToFollow: profileUserId
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