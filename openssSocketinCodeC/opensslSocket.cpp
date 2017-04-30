// opensslSocket.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "opensslSocket.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#define bcopy(b1,b2,len) (memmove((b2), (b1), (len)), (void) 0)
#define PRIVATE_KEY "private.pem"
#define PUBLIC_KEY "cert_public.pem"

#pragma comment(lib,"ws2_32.lib")
// The one and only application object

CWinApp theApp;

using namespace std;

SSL_CTX* InitCTX(void) {

	SSL_CTX *ctx;

	SSL_load_error_strings();
	ERR_load_BIO_strings();
	SSL_library_init();
	OpenSSL_add_all_algorithms();

	ctx = SSL_CTX_new(TLSv1_2_client_method());

	if (ctx == NULL)
	{
		ERR_print_errors_fp(stderr);
		printf("Eroor: %s\n", stderr);
		system("pause");
	}
	
	return ctx;
}

int openConnection() {

	int sd;
	struct  hostent *host;
	struct sockaddr_in addr;

	char *hostname = "debatedecide.fit.edu/proposals.php?organizationID=358&msg=&secure=on";
	int port = 443;

	if ((host = gethostbyname(hostname)) == NULL)
	{

		perror(hostname);
		system("pause");
	}

	if ((sd = socket(PF_INET, SOCK_STREAM, 0)) == -1) {

		perror("Socket");
		system("pause");
	}

	printf("Socket created.\n");

	bcopy(host->h_addr, &(addr.sin_addr), host->h_length);
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);

	if (connect(sd, (sockaddr *)&addr, sizeof(addr)) != 0) {

		perror("Connect");
		system("pause");
	}

	printf("Server connected.\n");

	return sd;
}

void showCert(SSL *ssl) {

	X509 *cert;
	cert = SSL_get_peer_certificate(ssl);
	if (cert != NULL) {

		printf("Get the certificate.\n");
		X509_free(cert);
	}
	else {

		printf("No certificate found.\n");
	}

}

int main()
{
    int nRetCode = 0;

	int sd;
	SSL_CTX *ctx;
	SSL *ssl;
	char *msg, buffer[1024];
	int length;

    HMODULE hModule = ::GetModuleHandle(nullptr);

    if (hModule != nullptr)
    {
        // initialize MFC and print and error on failure
        if (!AfxWinInit(hModule, nullptr, ::GetCommandLine(), 0))
        {
            // TODO: change error code to suit your needs
            wprintf(L"Fatal Error: MFC initialization failed\n");
            nRetCode = 1;
        }
        else
        {
			WSADATA wd;
			if (WSAStartup(0x0101, &wd) != 0)
			{
				printf("WSAStartup error.\n");
				system("pause");
			}

			ctx = InitCTX();

			if (SSL_CTX_use_certificate_file(ctx, PUBLIC_KEY, SSL_FILETYPE_PEM) != 1) {
				SSL_CTX_free(ctx);
				ERR_print_errors_fp(stderr);
			}

			if (SSL_CTX_use_PrivateKey_file(ctx, PRIVATE_KEY, SSL_FILETYPE_PEM) != 1) {
				SSL_CTX_free(ctx);
				ERR_print_errors_fp(stderr);
			}

			if (SSL_CTX_check_private_key(ctx) != 1) {
				SSL_CTX_free(ctx);
				ERR_print_errors_fp(stderr);
			}

			SSL_CTX_set_mode(ctx, SSL_MODE_AUTO_RETRY);

			sd = openConnection();
			ssl = SSL_new(ctx);
			SSL_set_fd(ssl, sd);
			if (SSL_connect(ssl) == -1) {

				ERR_print_errors_fp(stderr);
			}
			else {
				showCert(ssl);
				printf("Connect success.");
				msg = "GET / HTTP/1.1\r\n\r\n";
				length = SSL_write(ssl, msg, strlen(msg));


				printf("1111111");
				if (length < 0) {

					ERR_print_errors_fp(stderr);
					system("pause");
				}

				length = SSL_read(ssl, buffer, sizeof(buffer) - 1);
				if (length != 0) {

						printf("Got content: '%s'\n", buffer);
				}

			}

		finish:
			SSL_shutdown(ssl);
			SSL_free(ssl);
			closesocket(sd);
			SSL_CTX_free(ctx);
			WSACleanup();

        }
    }
    else
    {
        // TODO: change error code to suit your needs
        wprintf(L"Fatal Error: GetModuleHandle failed\n");
        nRetCode = 1;
    }


    return nRetCode;
}
