/**
* Nikolay Ivanov, 2115451i
* APH Excercise 1
* This is my own work as defined in the Academic Ethics 
* agreement I have signed
*/

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include "mentry.h"
#include "mlist.h"

#define SIZE 20
#define BUCKETCAPACITY 20  

typedef struct Bucket_node {
	MEntry *me;
	struct Bucket_node *next;
} Bucket_node;

typedef struct bucket_list{
	int size;
	Bucket_node *head;
}Bucket_list;

struct mlist {
	int capacity;
	Bucket_list **bucket;
};

int ml_verbose = 0;


/** 
* Function MList *ml_resize(int arg1) 
* Creates a new list(hashtable) with the specified size
*/
MList *ml_resize(int size){
	MList *ml;
	int i; /* for counter */

	if (ml_verbose){
		fprintf(stderr, "mlist: creating mailing list\n");
	}
	
	/* if invalid size return 0 */
	if (size<1){
		return 0; 
	}
	
	/* Allocate space for the new mailing_list */
	if ((ml = malloc(sizeof(MList))) == NULL){
		return 0;
	}

	if ((ml->bucket = malloc(size * sizeof(Bucket_list *))) == NULL){  
		return 0; 
	}
  
	ml->capacity = size;
	for(i=0; i<ml->capacity; i++){
		if ((ml->bucket[i] = (Bucket_list*)malloc(sizeof(Bucket_list))) == NULL){ 		
			return 0;
		}
		ml->bucket[i]->head = NULL; 
		ml->bucket[i]->size = 0;
		
	}

	return ml;
}

/**
* Function MList *ml_create(void)
* calls the ml_resize(arg1) function in 
* order to create the initial list(hashtable)
*/
MList *ml_create(void){
	return ml_resize(BUCKETCAPACITY);
}


/**
* Function int ml_add(MList **arg1, MEntry *arg2) 
* Adds a new mailing_entry to the list and returns 1 
* if successful or duplicate and 0 if an error has occured
*/

int ml_add(MList **ml, MEntry *me){ 

	Bucket_node *bucket_entry_new;
	Bucket_node *bucket_node_temp,*bucket_node_temp_next;
	MList *m_list = *ml;
	int i; /* for counter */
	unsigned int hashval;

	hashval = me_hash(me, m_list->capacity); /* compute the hash_val for the mailing entry */

	if (ml_verbose){
		fprintf(stderr, "mlist: ml_add() entered\n"); /* was not in mlistLL.c but for the verbose to be complete added here */
	}
	
	/* Check for duplicate entry */
    	if (ml_lookup(m_list, me) != NULL){
		return 1;
	}

	/* Allocate memory for the new element */
	if ((bucket_entry_new = (Bucket_node*)malloc(sizeof(Bucket_node))) == NULL){
		return 0;
	}
	bucket_entry_new->me = me;
	bucket_entry_new->next = NULL;
 	
    
	
    	/* If empty list set the head to the mailing_entry */
	if (m_list->bucket[hashval]->head == NULL){
		m_list->bucket[hashval]->head = bucket_entry_new;
		return 1;
	}

	/* If there is no place for the entry and resize is needed */
	if (m_list->bucket[hashval]->size >= SIZE){ 
		MList *ml_resized;
		ml_resized = ml_resize((m_list->capacity)*2); /* create new list(hashtable) with double the size */
		for(i=0; i<m_list->capacity; i++){ /* get the elements from the old list and copy them to the new one */
			for (bucket_node_temp = m_list->bucket[i]->head; bucket_node_temp != NULL;){
				bucket_node_temp_next = bucket_node_temp->next;
				ml_add(&ml_resized, bucket_node_temp->me); 
				free(bucket_node_temp); 
				bucket_node_temp = bucket_node_temp_next;	
			}
			/* free the alocated memory */
			free(m_list->bucket[i]); 
		}
		free(m_list->bucket);
		
		**ml = *ml_resized; /* make the link between the original list and the resized one by setting the old list(hashtable) pointer to point to the new list(hashtable) pointer */
		hashval = me_hash(me, ml_resized->capacity); /* get the new hashvalue for the mailing_entry */
		
		/* insert the new entry and increment the size */
		bucket_entry_new->next = ml_resized->bucket[hashval]->head; 
		ml_resized->bucket[hashval]->head = bucket_entry_new;
		ml_resized->bucket[hashval]->size++; 
		/*free(ml); */ /* for some reason it does not seem to work */
		ml = NULL;
		
		return 1;
	}

	/* insert the new entry and increment the size */
	bucket_entry_new->next = m_list->bucket[hashval]->head; 
	m_list->bucket[hashval]->head = bucket_entry_new; 
	m_list->bucket[hashval]->size++; 

	return 1;
}


/**
* Function MEntry *ml_lookup(MList *ml, MEntry *me)
* Checks if the current mailing_entry already exists in the
* list(hashtable) if duplicate, returns the found match
* if thre is no duplicate, return NULL
*/
MEntry *ml_lookup(MList *ml, MEntry *me){
	Bucket_node *iterator;
	unsigned int hashval = me_hash(me, ml->capacity);
	if (ml_verbose){
		fprintf(stderr, "mlist: ml_lookup() entered\n");
	}
	
	iterator = ml->bucket[hashval]->head;
	while(iterator!=NULL){
		if(me_compare(iterator->me,me)==0) return iterator->me; 
		iterator = iterator->next; 
	}
	
	return NULL;

}


/**
* Function void ml_destroy(MList *ml)
* destroys the mailing list and mailing entries inside it
*/
void ml_destroy(MList *ml){ 
	int i; /* for counter */
   	Bucket_node *bucket_node1;
	Bucket_node *bucket_node2;

	if (ml_verbose){
		fprintf(stderr, "mlist: ml_destroy() entered\n");
	}   
	/* If error with list */ 
	if (ml==NULL){
		return;
	}
 
	for(i=0; i<ml->capacity; i++){ 
		bucket_node1 = ml->bucket[i]->head;
		while(bucket_node1 != NULL ){
 			bucket_node2 = bucket_node1->next;
			if (bucket_node1->me != NULL){
				me_destroy(bucket_node1->me); /* destroy the mailing_entry */
			}
			free(bucket_node1); /* free the bucket_node */
			bucket_node1 = bucket_node2;

		}
		free(ml->bucket[i]); /* free the bucket */
	}

	free(ml->bucket); /* free the list of buckets */
	free(ml); /* free the whole list(hashtable) */
}

