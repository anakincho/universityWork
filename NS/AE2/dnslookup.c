#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>

void dns_lookup(char * host) {
    struct addrinfo hints, * ai;
    char addrstr[100];
    void * ptr;

    memset( & hints, 0, sizeof(hints));
    hints.ai_family = PF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;

    if (getaddrinfo(host, NULL, & hints, & ai) != 0) {
		freeaddrinfo(ai);
        perror("getaddrinfo");
		exit(1);
    }

    while (ai) {
        inet_ntop(ai -> ai_family, ai -> ai_addr -> sa_data, addrstr, 100);

        switch (ai -> ai_family) {
        case AF_INET:
            ptr = & ((struct sockaddr_in * ) ai -> ai_addr) -> sin_addr;
            break;
        case AF_INET6:
            ptr = & ((struct sockaddr_in6 * ) ai -> ai_addr) -> sin6_addr;
            break;
        }
        inet_ntop(ai -> ai_family, ptr, addrstr, 100);
        printf("%s IPv%d %s\n", host, ai -> ai_family == PF_INET6 ? 6 : 4, addrstr);
        ai = ai -> ai_next;
    }
	freeaddrinfo(ai);
}

int main(int argc, char * argv[]) {
    if (argc < 2)
        return -1;
	for(int i=1; i < argc; i++){
    	dns_lookup(argv[i]);
	}
	return 0;
}
