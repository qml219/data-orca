import type { UserDTO, Role } from "./user.dto";

// The User class implements the User FE domain model - think UserDTO, but with extra behavior methods. It doesn't simply extend the UserDTO interface because it needs more than additional attributes
// Upon login, a User object is constructed and lives in a User React State defined in AuthProvider. If User ever changes, AuthProvider rerenders, causing the AuthContext (what it contains), to be propagated downwards towards all consuming children elements, causing rerenders there. 
export class User {
    private readonly dto: UserDTO

    constructor(dto: UserDTO) {
        this.dto = dto;
    }

    // Getter property, not a getter function -> user.id, NOT user.id()
    get id() {
        return this.dto.id;
    }

    get username() {
        return this.dto.username;
    }

    get role() {
        return this.dto.role;
    }

    isAdmin(): boolean {
        return this.dto.role === "ADMIN"
    }
    
    displayName(): string {
        return this.dto.username;
    }
}