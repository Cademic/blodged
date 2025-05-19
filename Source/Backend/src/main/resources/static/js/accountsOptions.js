$(document).ready(() => {
    $("[data-bs-toggle=popover]").popover()
})
function prepUserUpdate(keyToUpdate) {
    let val = ''
    if (keyToUpdate === "username") val = $('#username').val()
    if (keyToUpdate === "password") {
        const userId = $("#userId").val()
        fetch("/accounts/auth", { // TODO spring security update
            method: "POST",
            body: JSON.stringify({
                userId: userId,
                password: $("#confirm-password").val()
            }),
            headers: {
                    "Content-type": "application/json; charset=UTF-8"
                }
        }).then(resp => {
            if (resp.status === 200) {
                sendUserUpdate(keyToUpdate, $("#password").val())
            } else {
                openPasswordModal()
                error("Incorrect password")
            }
        })
    }
    if (keyToUpdate === "email") val = $('#email').val()
    if (keyToUpdate === "bio") val = $('#bio').val()
    if (keyToUpdate === "private") val = $('#private').is(":checked")
    if (keyToUpdate === "gravatar") val = $("#gravatar").val()
    sendUserUpdate(keyToUpdate, val)
}
function sendUserUpdate(keyToUpdate, valueToUpdate) {
    const userId = $("#userId").val()
    fetch('/accounts/updateUser', {
        method: "POST",
        body: JSON.stringify({
            userId: userId, 
            keyToUpdate: keyToUpdate,
            valueToUpdate: valueToUpdate
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).then(async resp => {
        const text = await resp.text()
        if (resp.status === 200) {
            window.location.replace(window.location.origin + "/accounts/options")
        } else {
            error(text)
        }
    })
}
function openDeleteModal() {
    $("#confirmModal").modal('toggle')
}
function openPasswordModal() {
    $("#passwordModal").modal('toggle')
}
function confirmDeleteAcc() {
    let pass = $("#confirm-content").val()
    let uid = $("#userId").val()
    fetch("/accounts/auth", {
        method: "POST",
        body: JSON.stringify({
            userId: parseInt(uid),
            password: pass
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    }).then(async resp => {
        if (resp.status === 200) {
            fetch("/accounts/delete", {
                method: "DELETE",
                body: JSON.stringify({
                    userId: uid,
                    deletePosts: true
                }),
                headers: {
                    "Content-type": "application/json; charset=UTF-8"
                }
            }).then(async resp => {
                if (resp.status === 200) {
                    window.location.replace(window.location.origin + "/")
                } else {
                    let text = await resp.text()
                    alert(`Account could not be deleted for reason '${text}' - try logging in and retrying delete`)
                    window.location.replace(window.location.origin + "/")
                }
            })
        } else {
            $("#confirmModal").modal('toggle')
            error("Password incorrect")
        }
    })
}
function error(errStr) {
    const err = $("#err")
    err.text(`There was an error saving your changes! ${errStr}`)
    err.show()
    setTimeout(() => $('#err').hide(), 5000)
}
