AJS.$(document).ready(function(){
    AJS.$('body').append(
        '<section id="dark-mode-invite-dialog" class="aui-dialog2 aui-layer" role="dialog" aria-hidden="true">' +
            '<div class="dark-mode-image"><div><a href="profile" id="button-spin-1">Go to profile and enable dark mode</a></div><div class="hide-intro"><a href="profile" id="button-spin-2">Hide banner</a></div></div>' +
        '</section>');
    AJS.$('.hide-intro a').click(function() {
        AJS.dialog2("#dark-mode-invite-dialog").hide();
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
        return false;
    })

    AJS.dialog2("#dark-mode-invite-dialog").show();
})