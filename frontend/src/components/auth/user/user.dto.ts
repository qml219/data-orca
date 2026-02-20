export type Role = "ADMIN" | "USER" // Same as BE

// This class is a DTO for the data returned by the BE to the FE - it exists at the boundary of these 2 systems. Answer: What does the backend send; what do I serialize?
export interface UserDTO {
    id: string // UUID in the backend, but keeping as String so that FE is decoup from BE
    username: string
    role: Role
}