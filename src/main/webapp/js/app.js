// Auto-hide flash message
window.addEventListener("load", function () {
    var toast = document.querySelector(".toast");
    if (toast) {
        setTimeout(function () { toast.style.opacity = "0"; }, 3500);
        setTimeout(function () { toast.remove(); }, 4000);
    }
});

// Ask before approving / rejecting a request
function confirmAction(text) {
    return confirm(text);
}
