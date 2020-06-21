AJS.$(document).ready(function(){
    var form = document.getElementById('dark-mode-toggle-form');
    form.addEventListener('change', function(e) {
        var toggle = document.getElementById('enable-dark-mode');
        var isChecked = toggle.checked;     // new value of the toggle

        toggle.busy = true;
        $.post('?', { "dark-mode": isChecked ? "enabled" : "disabled" })
            .done(function () {
                location.reload();
            })
            .fail(function () {
                toggle.checked = !isChecked;
                console.error('display an error message');
            })
            .always(function () {
                toggle.busy = false;
            });
    });
})