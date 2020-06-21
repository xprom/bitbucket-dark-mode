AJS.$(document).ready(function(){
    AJS.$('body').append(
        '<section id="dark-mode-invite-dialog" class="aui-dialog2 aui-layer" role="dialog" aria-hidden="true">' +
            '<div class="dark-mode-image"><div><a href="profile" id="button-spin-1">Go to profile and enable dark mode</a></div><div class="hide-intro"><a href="profile" id="button-spin-2">Hide banner</a></div></div>' +
        '</section>');
    AJS.$('.hide-intro a').click(function() {
        AJS.dialog2("#dark-mode-invite-dialog").hide();
        return false;
    })

    AJS.dialog2("#dark-mode-invite-dialog").show();

    AJS.dialog2("#dark-mode-invite-dialog").on("hide", function() {
        $.post('plugins/servlet/dark-mode/profile', { "hide-banner": 2 });
    });
})