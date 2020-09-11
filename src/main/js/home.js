"use strict"

import React from "react";
import { Form, Nav, Navbar } from "react-bootstrap";
import ReactDOM from "react-dom";

class HomeNavBar extends React.Component {

    checkLoginState() {
        let loginCookie = document.cookie
            .split(";")
            .find(row => row.startsWith("JSESSIONID="))
    }

    render() {
        return (
            <Navbar bg="primary" variant="dark" fixed="top">
                <Navbar.Brand href="/">Relink</Navbar.Brand>
                <Nav className="mr-auto">
                    
                    <Nav.Link href="/login">Login</Nav.Link>
                </Nav>
            </Navbar>
        )
    }
}

ReactDOM.render(
    <HomeNavBar />,
    document.getElementById("homeNavBar")
)