/**
* Nikolay Ivanov, 2115451i
* APH Excercise 1
* This is my own work as defined in the Academic Ethics 
* agreement I have signed
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "mentry.h"

#define LINELENGTHMAX 256

/**
* Function char* stringRemoveNonAlphaNum(char *arg1)
* used to remove all non alpha-numeric characters and returns
* the string
*/
char* stringRemoveNonAlphaNum(char *str){
	unsigned long i = 0;
	unsigned long j = 0;
	char c;

	while ((c = str[i++]) != '\0'){
		if (isalnum(c)){
	    		str[j++] = (char)toupper(c); /*use Upper case incase the entry in the file is with random cases*/
		}
	}
	str[j] = '\0';
	return str;
}


/** 
* Function MEntry *me_get(File *arg1)
* Returns the next mailing entry until 
* it reached the EOF
*/
MEntry *me_get(FILE *fd){
	struct mentry * new_entry;
	char line1[LINELENGTHMAX], line2[LINELENGTHMAX], line3[LINELENGTHMAX];
	int i; /* for counter */

	/* Allocate memory for the new_entry and its fields */
	if ((new_entry = (struct mentry *)malloc(sizeof(struct mentry))) == NULL) return NULL;		
		
	if ((new_entry->surname = malloc(sizeof(char) * LINELENGTHMAX)) == NULL) return NULL;
	if ((new_entry->postcode = malloc(sizeof(char) * LINELENGTHMAX)) == NULL) return NULL;
	if ((new_entry->full_address = malloc(sizeof(char) * (LINELENGTHMAX * 3))) == NULL) return NULL;
	
	/*if any of the lines(name,address,postcode) exceeds the max line length then destroy entry and return NULL*/
	if (fgets(line1, LINELENGTHMAX, fd) == NULL){
		me_destroy(new_entry);
		return NULL;
	}
	if (fgets(line2, LINELENGTHMAX, fd) == NULL){
		me_destroy(new_entry);
		return NULL;
	}
	if (fgets(line3, LINELENGTHMAX, fd) == NULL){
		me_destroy(new_entry);
		return NULL;
	}

	/*append the full information in full_address*/
	strcpy(new_entry->full_address,line1);
	strcat(new_entry->full_address,line2);
	strcat(new_entry->full_address,line3); 

	for (i = 0; line1[i] != ','; i++){
    		new_entry->surname[i] = (char)toupper(line1[i]); /*puts surname into lower case and into the struct */
	}
	new_entry->surname[i++] = '\0'; /* append the terminating symbol */

	new_entry->house_number = strtol(line2, NULL, 10); /* takes housenumber of of the second line and into the struct */
	
	/*removes the non alphanumeric chars and writes the postcode to the struct*/
	strcpy(new_entry->postcode,stringRemoveNonAlphaNum(line3));
	
	return new_entry;
}

/**
* Function unsigned long compute_hash(char* arg1)
* Takes a string and returns the computed hashvalue
*/
unsigned long compute_hash(char *string){ 
	unsigned long hashval;
	for (hashval = 0; *string != '\0'; string++){
		hashval = *string + 31 * hashval;
	}
	return hashval;
}

/**
* Function unsigned long me_hash(MEntry* arg1, unsigned long arg2)
* Takes a mailing_entry and a size, calculates the hashvalue for
* surname+postcode+house_number and returns
* a value between 0 and size-1
*/
unsigned long me_hash(MEntry *me, unsigned long size){
	unsigned long hashval = 0;

	hashval = compute_hash(me->surname); 
	hashval += compute_hash(me->postcode); 
	hashval += me->house_number; 

	return hashval % size; 
}	

/**
* Function void me_print(Mentry* arg1, FILE *arg2)
* writes the full address of the mailing entry into file arg2
*/
void me_print(MEntry *me, FILE *fd){

	fprintf(fd, "%s", me->full_address); 

}

/**
* Function int me_compare(Mentry *arg1, MEntry *arg2)
* Compares the two mailing entries and returns <0, 0, >0
*/
int me_compare(MEntry *me1, MEntry *me2){
	char me_string1[LINELENGTHMAX], me_string2[LINELENGTHMAX];
	sprintf(me_string1, "%d\n", me1->house_number); /*convert house_number to str and append it */
	strcat(me_string1, me1->surname);
	strcat(me_string1, me1->postcode);
	sprintf(me_string2, "%d\n", me2->house_number); /*convert house_number to str and append it*/
	strcat(me_string2, me2->surname);
	strcat(me_string2, me2->postcode);

	return strcmp(me_string1, me_string2);

}


/**
* Function me_destroy(MEntry *arg1)
* Destroys the mailing_entry and frees 
* the allocated memory
*/
void me_destroy(MEntry *me){
	free(me->surname);
	free(me->postcode);
	free(me->full_address);
	free(me);
}



