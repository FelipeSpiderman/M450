import React from 'react';
import './AddIngredient.css';
import {Button, Col, Form, Row} from "react-bootstrap";

const AddIngredient = ({ingredient, updateIngredient, removeIngredient}) => {

    const handleChange = (event) => {
        const {name, value} = event.target;
        updateIngredient({...ingredient, [name]: value});
    };

    return (
        <Row>
            <Col>
                <Form.Group className="mb-1">
                    <Form.Control
                        name="name"
                        value={ingredient.name}
                        onChange={handleChange}
                        placeholder="Name"
                    />
                </Form.Group>
            </Col>
            <Col>
                <Form.Group className="mb-1">
                    <Form.Select
                        name="unit"
                        value={ingredient.unit}
                        onChange={handleChange}
                    >
                        <option value="PIECE">PIECE</option>
                        <option value="GRAMM">GRAMM</option>
                        <option value="KILOGRAMM">KILOGRAMM</option>
                        <option value="LITRE">LITRE</option>
                        <option value="DECILITRE">DECILITRE</option>
                    </Form.Select>
                </Form.Group>
            </Col>
            <Col>
                <Form.Group className="mb-1">
                    <Form.Control
                        type="number"
                        min="1"
                        step="1"
                        name="amount"
                        value={ingredient.amount}
                        onChange={handleChange}
                        placeholder="Quantity"
                    />
                </Form.Group>
            </Col>
            <Col xs={1}>
                <Button
                    type="button"
                    onClick={() => removeIngredient(ingredient)}
                    variant='outline-dark'
                    className="mb-1"
                >x</Button>
            </Col>
        </Row>
    );
};

export default AddIngredient;
