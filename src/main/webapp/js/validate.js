// Client-side validation for the registration form
function toggleDonorFields() {
    var isDonor = document.getElementById("role").value === "DONOR";
    document.getElementById("donorFields").style.display = isDonor ? "block" : "none";
}

function validateRegister(form) {
    if (form.password.value !== form.confirm.value) {
        alert("Passwords do not match.");
        return false;
    }
    if (form.role.value === "DONOR") {
        if (form.name.value.trim() === "") { alert("Name is required."); return false; }
        var age = parseInt(form.age.value, 10);
        if (isNaN(age) || age < 18 || age > 65) { alert("Age must be between 18 and 65."); return false; }
        if (!/^\d{10}$/.test(form.phone.value)) { alert("Phone must be 10 digits."); return false; }
    }
    return true;
}
