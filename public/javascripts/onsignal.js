<script src="https://cdn.onesignal.com/sdks/OneSignalSDK.js" async='async'></script>
		<script>
			var OneSignal = window.OneSignal || [];
			OneSignal.push(["init", {
				appId: "7257d6a5-5298-43ee-b0d5-e2b89cfaab11",
				autoRegister: true, /* Set to true to automatically prompt visitors */
				subdomainName: 'https://seupedido.onesignal.com',   
				persistNotification: true,
			httpPermissionRequest: {
				enable: true
			},
			httpPermissionRequest: {
	            modalTitle: 'Obrigado pela inscrição',
	            modalMessage: "Agora você receberá nossas notificações. Você pode cancelar sua inscrição a qualquer momento.",
	            modalButtonText: 'Fechar',
	            useCustomModal: true        
			},
	        promptOptions: {
				/* Change bold title, limited to 30 characters */
				siteName: 'Confirmação de recebimento',
				/* Subtitle, limited to 90 characters */
				actionMessage: "Confirme para receber artigos úteis sobre tecnologia.",
				/* Example notification title */
				exampleNotificationTitle: 'Este é um exemplo de notificação',
				/* Example notification message */
				exampleNotificationMessage: 'Aqui vai a descrição da notificação...',
				/* Text below example notification, limited to 50 characters */
				exampleNotificationCaption: 'Obs.: Você poderá cancelar em qualquer momento!',
				/* Accept button text, limited to 15 characters */
				acceptButtonText: "EU QUERO! :)",
				/* Cancel button text, limited to 15 characters */
				cancelButtonText: "EU NÃO QUERO!"
			},
			welcomeNotification: {
				"title": "Acompanhe Seu Pedido",
				"message": "Obrigado pela inscrição",
				 "url": "https://acompanheseupedido.com"
			}
	    }]);
    </script>
    
    <script>
    var OneSignal = OneSignal || [];
    function subscribe() {
    	OneSignal.push(["registerForPushNotifications"]);
    	OneSignal.push(function() {
      		OneSignal.sendTags({homelead: 'homelead'});
      		return false;
       });
       event.preventDefault();
    }

    /* This example assumes you've already initialized OneSignal */
    OneSignal.push(function() {
        // If we're on an unsupported browser, do nothing
        if (!OneSignal.isPushNotificationsSupported()) {
            return;
        }
        OneSignal.isPushNotificationsEnabled(function(isEnabled) {
            if (isEnabled) {
                // The user is subscribed to notifications
            } else {
                subscribe();
            }
        });
    });
</script>
<script>
 (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
 (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
 m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
 })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

 ga('create', 'UA-91777562-1', 'auto');
 ga('send', 'pageview');

</script>