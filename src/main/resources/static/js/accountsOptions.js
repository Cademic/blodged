function prepUserUpdate(keyToUpdate) {
    const userId = $("#userId").val(); // Common user ID retrieval
    let val = "";

    switch (keyToUpdate) {
        case "username":
            val = $("#username").val();
            sendUserUpdate(keyToUpdate, val);
            break;

        case "password": {
            const confirmPassword = $("#confirm-password").val();
            fetch("/accounts/auth", {
                method: "POST",
                body: JSON.stringify({ userId, password: confirmPassword }),
                headers: { "Content-type": "application/json; charset=UTF-8" }
            })
                .then(resp => {
                    if (resp.status === 200) {
                        const newPassword = $("#password").val();
                        sendUserUpdate(keyToUpdate, newPassword);
                    } else {
                        openPasswordModal();
                        error("Incorrect password");
                    }
                })
                .catch(err => {
                    console.error("Error authenticating password:", err);
                    error("An unexpected error occurred during authentication.");
                });
            break;
        }

        case "email":
            val = $("#email").val();
            sendUserUpdate(keyToUpdate, val);
            break;

        case "bio":
            val = $("#bio").val();
            sendUserUpdate(keyToUpdate, val);
            break;

        case "private":
            val = $("#private").is(":checked");
            sendUserUpdate(keyToUpdate, val);
            break;

        default:
            error("Invalid key to update");
            break;
    }
}

function sendUserUpdate(keyToUpdate, valueToUpdate) {
    const userId = $("#userId").val();
    fetch("/accounts/updateUser", {
        method: "POST",
        body: JSON.stringify({ userId, keyToUpdate, valueToUpdate }),
        headers: { "Content-type": "application/json; charset=UTF-8" }
    })
        .then(async resp => {
            const text = await resp.text();
            if (resp.status === 200) {
                window.location.replace(`${window.location.origin}/accounts/options`);
            } else {
                error(text);
            }
        })
        .catch(err => {
            console.error("Error updating user:", err);
            error("An unexpected error occurred while updating.");
        });
}

function openDeleteModal() {
    $("#confirmModal").modal("toggle");
}

function openPasswordModal() {
    $("#passwordModal").modal("toggle");
}

function confirmDeleteAcc() {
    const deletePosts = $("#delete-posts").is(":checked");
    const confirmPassword = $("#confirm-content").val();
    const oldPassword = $("#old-password").val();
    const userId = $("#userId").val();

    if (confirmPassword === oldPassword) {
        fetch("/accounts/delete", {
            method: "DELETE",
            body: JSON.stringify({ userId, deletePosts }),
            headers: { "Content-type": "application/json; charset=UTF-8" }
        })
            .then(async resp => {
                if (resp.status === 200) {
                    window.location.replace(`${window.location.origin}/`);
                } else {
                    const text = await resp.text();
                    alert(`Account could not be deleted: '${text}' - try logging in and retrying delete.`);
                    window.location.replace(`${window.location.origin}/`);
                }
            })
            .catch(err => {
                console.error("Error deleting account:", err);
                alert("An unexpected error occurred while attempting to delete the account.");
            });
    } else {
        $("#confirmModal").modal("toggle");
        error("Password incorrect");
    }
}

function error(errStr) {
    $("#err").text(`There was an error saving your changes! ${errStr}`);
    $("#err").show();
    setTimeout(() => $("#err").hide(), 5000);
}
