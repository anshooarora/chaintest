export class Page<T> {
    content: T[] = [];
    empty: boolean = true;
    first: boolean = true;
    last: boolean = true;
    number: number = 0;
    numberOfElements: number = 0;
    size: number = 0;
    totalElements: number = 0;
    totalPages: number = 0;
}