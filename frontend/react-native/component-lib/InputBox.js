/* InputBox.js
** Customizied component in which user can insert value.
*/

import { View, StyleSheet, Text, TextInput } from "react-native";
import { useState } from "react";
import { FontAwesome } from '@expo/vector-icons';

export const KeyboardTypes = {
    DEFAULT: 'default',
    EMAIL: 'email-address',
}
export const ReturnKeyTypes = {
    DONE: 'done',
    NEXT: 'next',
}

const InputBox = ({title, placeholder, keyboardType, returnKeyType, secureTextEntry, value, onChangeText, iconName, ...props}) => {
    const [isFocused, setIsFocused] = useState(false);
    return (
        <View style={styles.container}>
            <Text style={[styles.title, isFocused && styles.focusedTitle, value && styles.hasValueTitle]}>{title}</Text>
            <View>
                <TextInput 
                    style={[styles.input, isFocused && styles.focusedInput, value && styles.hasValueInput]}
                    value={value}
                    onChangeText={onChangeText}
                    placeholder={placeholder ?? title}
                    autoCapitalize={'none'}
                    autoCorrect={false}
                    keyboardType={keyboardType}
                    returnKeyType={returnKeyType}
                    secureTextEntry={secureTextEntry}
                    keyboardAppearance={'light'}
                    onBlur={()=>setIsFocused(false)}
                    onFocus={()=>setIsFocused(true)}
                />
                <View style={styles.icon}>
                    <FontAwesome name={iconName} size={20} color={
                        (()=>{
                            switch(true) {
                                case isFocused:
                                    return '#2563eb';
                                default:
                                    return '#a3a3a3'; 
                            }
                        })()
                    } />
                </View>
            </View>
        </View>
      );
};

const styles = StyleSheet.create({
    container: {
        width: '100%',
        paddingHorizontal: 20,
        marginVertical: 20,
    },
    title: {
        marginBottom: 4,
        color: '#a3a3a3', // GRAY
    },
    focusedTitle: {
        fontWeight: '600',
        color: '#2563eb', 
    },
    hasValueTitle: {
        color: '#000000', // BLACK
    },
    input: {
        borderWidth: 1,
        borderRadius: 8,
        paddingHorizontal: 20,
        height: 42,
        paddingLeft: 30,
    },
    focusedInput: {
        borderColor: '#2563eb',
    },
    hasValueInput: {
        color: '#000000',
        borderColor: '#000000',
    },
    icon: {
        position: 'absolute',
        left: 8,
        height: '100%',
        justifyContent: 'center'
    }
});

export default InputBox;

