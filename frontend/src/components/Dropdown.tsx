import React, { useState } from 'react'

interface DropdownProps {
    options: string[]
    initialValue?: string
    onChange?: (value: string) => void
}

export default function Dropdown({
                                     options,
                                     initialValue,
                                     onChange
                                 }: DropdownProps) {
    const initial = initialValue && options.includes(initialValue)
        ? initialValue
        : options[0]

    const [selected, setSelected] = useState(initial)

    const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const value = e.target.value
        setSelected(value)
        onChange?.(value)
    }

    return (
        <select value={selected} onChange={handleChange}>
            {options.map(opt => (
                <option key={opt} value={opt}>
                    {opt}
                </option>
            ))}
        </select>
    )
}
